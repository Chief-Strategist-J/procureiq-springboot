package com.procureiq.springboot_app.features.auth.dto;

public class LoginResponse {
    private String token = "";
    private UserResponse user = new UserResponse();

    public LoginResponse() {}

    public LoginResponse(String token, UserResponse user) {
        this.token = token != null ? token : "";
        this.user = user != null ? user : new UserResponse();
    }

    public String getToken() {
        return token != null ? token : "";
    }

    public void setToken(String token) {
        this.token = token != null ? token : "";
    }

    public UserResponse getUser() {
        return user != null ? user : new UserResponse();
    }

    public void setUser(UserResponse user) {
        this.user = user != null ? user : new UserResponse();
    }
}
