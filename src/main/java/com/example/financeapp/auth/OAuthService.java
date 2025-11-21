package com.example.financeapp.auth;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.awt.Desktop;
import java.io.IOException;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;

public class OAuthService {

    // ---------- CALLBACK ----------
    public interface OAuthCallback {
        void onSuccess(String tokenJson);
        void onError(String message);
    }

    private static OAuthCallback globalCallback;

    // ---------- GOOGLE CONFIG ----------
    private static final String CLIENT_ID =
            "901864631846-vei0uigfmdttd9fpop06250me40r7v9e.apps.googleusercontent.com";

    private static final String CLIENT_SECRET =
            "client_secret_901864631846-vei0uigfmdttd9fpop06250me40r7v9e.apps.googleusercontent.com";

    private static final String AUTH_ENDPOINT  = "https://accounts.google.com/o/oauth2/v2/auth";
    private static final String TOKEN_ENDPOINT = "https://oauth2.googleapis.com/token";

    private static final String SCOPES = String.join(" ",
            "openid",
            "email",
            "profile"
    );

    // ---------- PUBLIC API ----------

    public static void loginWithGoogle(OAuthCallback callback) {
        globalCallback = callback;

        new Thread(() -> {
            try {
                runGoogleOAuthFlow();
            } catch (Exception e) {
                if (globalCallback != null)
                    globalCallback.onError("Exception: " + e.getMessage());
            }
        }, "Google-OAuth-Thread").start();
    }

    public static void loginWithApple(OAuthCallback callback) {
        if (callback != null) {
            callback.onError("Apple OAuth not implemented yet.");
        }
    }

    @Deprecated
    public static void loginWithGoogle() {
        loginWithGoogle(null);
    }

    // ---------- INTERNAL GOOGLE FLOW ----------

    private static void runGoogleOAuthFlow() throws Exception {

        int port = findFreePort();
        String redirectUri = "http://127.0.0.1:" + port + "/oauth2callback";

        String state = UUID.randomUUID().toString();

        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", port), 0);
        OAuthCallbackHandler callbackHandler = new OAuthCallbackHandler(state);
        server.createContext("/oauth2callback", callbackHandler);
        server.start();

        String authUrl = buildAuthUrl(redirectUri, state);
        openBrowser(authUrl);

        String code = callbackHandler.waitForCode(Duration.ofMinutes(5));
        server.stop(0);

        if (code == null) {
            if (globalCallback != null)
                globalCallback.onError("No authorization code received.");
            return;
        }

        String tokenResponse = exchangeCodeForTokens(code, redirectUri);

        if (globalCallback != null) {
            globalCallback.onSuccess(tokenResponse);
        }
    }

    private static String buildAuthUrl(String redirectUri, String state) throws Exception {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("client_id", CLIENT_ID);
        params.put("redirect_uri", redirectUri);
        params.put("response_type", "code");
        params.put("scope", SCOPES);
        params.put("access_type", "offline");
        params.put("include_granted_scopes", "true");
        params.put("state", state);

        return AUTH_ENDPOINT + "?" + buildQuery(params);
    }

    private static String exchangeCodeForTokens(String code, String redirectUri) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        Map<String, String> form = new LinkedHashMap<>();
        form.put("code", code);
        form.put("client_id", CLIENT_ID);
        form.put("client_secret", CLIENT_SECRET);
        form.put("redirect_uri", redirectUri);
        form.put("grant_type", "authorization_code");

        String body = buildQuery(form);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(TOKEN_ENDPOINT))
                .timeout(Duration.ofSeconds(30))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        return response.body();
    }

    // ---------- UTILITY ----------

    private static int findFreePort() throws IOException {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        }
    }

    private static void openBrowser(String url)
            throws IOException, URISyntaxException {

        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().browse(new URI(url));
        } else {
            System.out.println("Open manually: " + url);
        }
    }

    private static String buildQuery(Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        params.forEach((k, v) -> {
            if (!sb.isEmpty()) sb.append('&');
            sb.append(URLEncoder.encode(k, StandardCharsets.UTF_8));
            sb.append('=');
            sb.append(URLEncoder.encode(v, StandardCharsets.UTF_8));
        });
        return sb.toString();
    }

    // ---------- CALLBACK HANDLER ----------

    private static class OAuthCallbackHandler implements HttpHandler {

        private final String expectedState;
        private String code;
        private String error;
        private final Object lock = new Object();

        OAuthCallbackHandler(String expectedState) {
            this.expectedState = expectedState;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            URI uri = exchange.getRequestURI();
            Map<String, String> params = parseQuery(uri.getRawQuery());

            String state = params.get("state");
            String receivedCode = params.get("code");
            String receivedError = params.get("error");

            String html;

            if (receivedError != null) {
                error = receivedError;
                html = "<h2>Login failed</h2>";
            } else if (!Objects.equals(state, expectedState)) {
                error = "State mismatch";
                html = "<h2>State mismatch</h2>";
            } else if (receivedCode != null) {
                code = receivedCode;
                html = "<h2>Login successful</h2>";
            } else {
                error = "No code provided";
                html = "<h2>No authorization code found</h2>";
            }

            byte[] bytes = html.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, bytes.length);
            exchange.getResponseBody().write(bytes);
            exchange.close();

            synchronized (lock) {
                lock.notifyAll();
            }
        }

        String waitForCode(Duration timeout) {
            long end = System.currentTimeMillis() + timeout.toMillis();
            synchronized (lock) {
                while (code == null && error == null && System.currentTimeMillis() < end) {
                    try { lock.wait(500); } catch (InterruptedException ignored) {}
                }
            }
            if (error != null) {
                System.out.println("OAuth Error: " + error);
            }
            return code;
        }

        private Map<String, String> parseQuery(String query) {
            Map<String, String> map = new HashMap<>();
            if (query == null) return map;

            for (String pair : query.split("&")) {
                String[] kv = pair.split("=", 2);
                String key = URLDecoder.decode(kv[0], StandardCharsets.UTF_8);
                String value = kv.length > 1
                        ? URLDecoder.decode(kv[1], StandardCharsets.UTF_8)
                        : "";
                map.put(key, value);
            }
            return map;
        }
    }
}