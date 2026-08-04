package com.procureiq.springboot_app.features.auth.dto.response;

public class UserResponse {
    private Long id = 0L;
    private String username = "";
    private String email = "";
    private String role = "user";

    public UserResponse() {}

    public UserResponse(Long id, String username, String email) {
        this.id = id != null ? id : 0L;
        this.username = username != null ? username : "";
        this.email = email != null ? email : "";
        this.role = "user";
    }

    public UserResponse(Long id, String username, String email, String role) {
        this.id = id != null ? id : 0L;
        this.username = username != null ? username : "";
        this.email = email != null ? email : "";
        this.role = role != null ? role : "user";
    }

    public Long getId() {
        return id != null ? id : 0L;
    }

    public void setId(Long id) {
        this.id = id != null ? id : 0L;
    }

    public String getUsername() {
        return username != null ? username : "";
    }

    public void setUsername(String username) {
        this.username = username != null ? username : "";
    }

    public String getEmail() {
        return email != null ? email : "";
    }

    public void setEmail(String email) {
        this.email = email != null ? email : "";
    }

    public String getRole() {
        return role != null ? role : "user";
    }

    public void setRole(String role) {
        this.role = role != null ? role : "user";
    }
}
