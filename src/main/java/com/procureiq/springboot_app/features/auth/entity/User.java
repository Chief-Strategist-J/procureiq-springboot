package com.procureiq.springboot_app.features.auth.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username = "";

    @Column(nullable = false)
    private String password = "";

    @Column(unique = true, nullable = false)
    private String email = "";

    @Column(name = "reset_token")
    private String resetToken = "";

    @Column(name = "reset_token_expiry")
    private java.time.LocalDateTime resetTokenExpiry = java.time.LocalDateTime.of(1970, 1, 1, 0, 0);

    @Column(name = "email_verified")
    private boolean emailVerified = false;

    @Column(name = "verification_token")
    private String verificationToken = "";

    public User() {}

    public User(String username, String password, String email) {
        this.username = username != null ? username : "";
        this.password = password != null ? password : "";
        this.email = email != null ? email : "";
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

    public String getResetToken() {
        return resetToken != null ? resetToken : "";
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken != null ? resetToken : "";
    }

    public java.time.LocalDateTime getResetTokenExpiry() {
        return resetTokenExpiry != null ? resetTokenExpiry : java.time.LocalDateTime.of(1970, 1, 1, 0, 0);
    }

    public void setResetTokenExpiry(java.time.LocalDateTime resetTokenExpiry) {
        this.resetTokenExpiry = resetTokenExpiry != null ? resetTokenExpiry : java.time.LocalDateTime.of(1970, 1, 1, 0, 0);
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public String getVerificationToken() {
        return verificationToken != null ? verificationToken : "";
    }

    public void setVerificationToken(String verificationToken) {
        this.verificationToken = verificationToken != null ? verificationToken : "";
    }
}
