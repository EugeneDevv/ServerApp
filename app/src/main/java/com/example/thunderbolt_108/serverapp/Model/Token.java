package com.example.thunderbolt_108.serverapp.Model;

public class Token {
    public String token;
    public boolean isServerToken;

    public Token() {

    }

    public Token(String token, boolean isServerToken) {
        this.token=token;
        this.isServerToken=isServerToken;

    }

    public void setServerToken(boolean serverToken) {
        isServerToken = serverToken;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isServerToken() {
        return isServerToken;
    }

    public String getToken() {
        return token;
    }
}
