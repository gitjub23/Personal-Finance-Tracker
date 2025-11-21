package com.example.financeapp.auth;

import fi.iki.elonen.NanoHTTPD;

import java.util.Map;

public class OAuthCallBackServer extends NanoHTTPD {

    public interface OAuthListener {
        void onSuccess(String code);
        void onError(String error);
    }

    private final OAuthListener listener;

    public OAuthCallBackServer(int port, OAuthListener listener) {
        super(port);
        this.listener = listener;
    }

    @Override
    public Response serve(IHTTPSession session) {
        if (session.getMethod() == Method.GET &&
                session.getUri().equals("/oauth/callback")) {

            Map<String, String> params = session.getParms();

            String code = params.get("code");
            String error = params.get("error");

            if (error != null) {
                listener.onError(error);
                return newFixedLengthResponse(
                        NanoHTTPD.Response.Status.OK,
                        "text/html",
                        "<html><body><h2>Login Failed</h2>" +
                                "<p>Error: " + error + "</p>" +
                                "<p>You may close this window.</p>" +
                                "</body></html>"
                );
            }

            if (code != null) {
                listener.onSuccess(code);
                return newFixedLengthResponse(
                        NanoHTTPD.Response.Status.OK,
                        "text/html",
                        "<html><body><h2>Login Successful!</h2>" +
                                "<p>You may close this window.</p>" +
                                "</body></html>"
                );
            }
        }

        return newFixedLengthResponse("Invalid request.");
    }
}