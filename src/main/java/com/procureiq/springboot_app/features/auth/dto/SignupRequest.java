package com.procureiq.springboot_app.features.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public class SignupRequest {
    @NotBlank(message = "Username cannot be blank")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username = "";

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password = "";

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Invalid email address format")
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
