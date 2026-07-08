package com.procureiq.springboot_app.features.auth.dto;

public class LoginRequest {
    private String username = "";
    private String password = "";

    public LoginRequest() {}

    public LoginRequest(String username, String password) {
        this.username = username != null ? username : "";
        this.password = password != null ? password : "";
    }

    public String getUsername() {
        return username != null ? username : "";
    }

    public void setUsername(String username) {
        this.username = username != null ? username : "";
    }

    public String getPassword() {
        return password != null ? password : "";
    }

    public void setPassword(String password) {
        this.password = password != null ? password : "";
    }
}
