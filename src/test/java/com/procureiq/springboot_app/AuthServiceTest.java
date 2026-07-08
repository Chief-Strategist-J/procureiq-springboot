package com.procureiq.springboot_app;

import com.procureiq.springboot_app.features.auth.dto.*;
import com.procureiq.springboot_app.features.auth.entity.User;
import com.procureiq.springboot_app.features.auth.repository.UserRepository;
import com.procureiq.springboot_app.features.auth.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setup() {
        userRepository.deleteAll();
    }

    @Test
    public void testSignupAndLogin() {
        SignupRequest signupRequest = new SignupRequest("testuser", "password123", "test@example.com");
        UserResponse signupResponse = authService.signup(signupRequest);

        assertNotNull(signupResponse.getId());
        assertEquals("testuser", signupResponse.getUsername());
        assertEquals("test@example.com", signupResponse.getEmail());

        LoginRequest loginRequest = new LoginRequest("testuser", "password123");
        LoginResponse loginResponse = authService.login(loginRequest);

        assertNotNull(loginResponse.getToken());
        assertEquals("testuser", loginResponse.getUser().getUsername());
    }

    @Test
    public void testSignupDuplicateUsername() {
        SignupRequest signupRequest = new SignupRequest("testuser", "password123", "test@example.com");
        authService.signup(signupRequest);

        SignupRequest duplicateRequest = new SignupRequest("testuser", "password456", "different@example.com");
        assertThrows(IllegalArgumentException.class, () -> {
            authService.signup(duplicateRequest);
        });
    }

    @Test
    public void testForgotPasswordAndResetPassword() {
        SignupRequest signupRequest = new SignupRequest("resetuser", "password123", "reset@example.com");
        authService.signup(signupRequest);

        // 1. Trigger forgot password
        ForgotPasswordRequest forgotRequest = new ForgotPasswordRequest("reset@example.com");
        authService.forgotPassword(forgotRequest);

        // 2. Fetch reset token from repository
        User user = userRepository.findByEmail("reset@example.com").orElseThrow();
        String token = user.getResetToken();
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertNotNull(user.getResetTokenExpiry());

        // 3. Reset password
        ResetPasswordRequest resetRequest = new ResetPasswordRequest(token, "newpassword123");
        authService.resetPassword(resetRequest);

        // 4. Verify login succeeds with the new password
        LoginRequest loginRequest = new LoginRequest("resetuser", "newpassword123");
        LoginResponse loginResponse = authService.login(loginRequest);
        assertNotNull(loginResponse.getToken());
        assertEquals("resetuser", loginResponse.getUser().getUsername());
    }
}
