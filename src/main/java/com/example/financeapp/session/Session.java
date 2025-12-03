package com.example.financeapp.session;

import com.example.financeapp.models.User;

public class Session {

    private static User currentUser;
    private static String oauthToken;

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static void setOAuthLogin(String token) {
        oauthToken = token;
    }

    public static boolean isLoggedIn() {
        return currentUser != null || oauthToken != null;
    }

    public static void logout() {
        currentUser = null;
        oauthToken = null;
    }
}