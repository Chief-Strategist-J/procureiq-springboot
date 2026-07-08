package com.procureiq.springboot_app.features.auth.dto;

public class SignupRequest {
    private String username = "";
    private String password = "";
    private String email = "";

    public SignupRequest() {}

    public SignupRequest(String username, String password, String email) {
        this.username = username != null ? username : "";
        this.password = password != null ? password : "";
        this.email = email != null ? email : "";
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

    public String getEmail() {
        return email != null ? email : "";
    }

    public void setEmail(String email) {
        this.email = email != null ? email : "";
    }
}
