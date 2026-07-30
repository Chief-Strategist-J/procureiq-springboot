package com.procureiq.springboot_app;

import com.procureiq.springboot_app.features.auth.dto.request.*;
import com.procureiq.springboot_app.features.auth.dto.response.*;
import com.procureiq.springboot_app.features.auth.entity.User;
import com.procureiq.springboot_app.features.auth.repository.UserRepository;
import com.procureiq.springboot_app.features.auth.service.AuthService;
import com.procureiq.springboot_app.infra.config.AppProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AppProperties appProperties;

    private AuthService authService;

    @BeforeEach
    public void setup() {
        when(appProperties.getJwtSecret()).thenReturn("test-secret-key-12345");
        when(appProperties.getJwtExpirationMs()).thenReturn(86400000L);
        authService = new AuthService(userRepository, passwordEncoder, appProperties);
    }

    @Test
    public void testSignupSuccess() {
        SignupRequest signupRequest = new SignupRequest("testuser", "password123", "test@example.com");
        User mockUser = new User("testuser", "encoded_pass", "test@example.com");
        mockUser.setId(1L);

        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encoded_pass");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        UserResponse response = authService.signup(signupRequest);

        assertEquals(1L, response.getId());
        assertEquals("testuser", response.getUsername());
        assertEquals("test@example.com", response.getEmail());
    }

    @Test
    public void testSignupDuplicateUsername() {
        SignupRequest signupRequest = new SignupRequest("testuser", "password123", "test@example.com");
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> authService.signup(signupRequest));
    }

    @Test
    public void testLoginSuccess() {
        LoginRequest loginRequest = new LoginRequest("testuser", "password123");
        User mockUser = new User("testuser", "encoded_pass", "test@example.com");
        mockUser.setId(1L);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("password123", "encoded_pass")).thenReturn(true);

        LoginResponse response = authService.login(loginRequest);

        assertNotNull(response.getToken());
        assertEquals("testuser", response.getUser().getUsername());
    }

    @Test
    public void testForgotPasswordSuccess() {
        ForgotPasswordRequest forgotRequest = new ForgotPasswordRequest("reset@example.com");
        User mockUser = new User("resetuser", "pass", "reset@example.com");

        when(userRepository.findByEmail("reset@example.com")).thenReturn(Optional.of(mockUser));

        authService.forgotPassword(forgotRequest);

        verify(userRepository).save(mockUser);
        assertNotNull(mockUser.getResetToken());
        assertFalse(mockUser.getResetToken().isEmpty());
    }

    @Test
    public void testResetPasswordSuccess() {
        ResetPasswordRequest resetRequest = new ResetPasswordRequest("valid-token", "newpassword123");
        User mockUser = new User("resetuser", "old_pass", "reset@example.com");
        mockUser.setResetToken("valid-token");
        mockUser.setResetTokenExpiry(LocalDateTime.now().plusHours(1));

        when(userRepository.findByResetToken("valid-token")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.encode("newpassword123")).thenReturn("new_encoded_pass");

        authService.resetPassword(resetRequest);

        verify(userRepository).save(mockUser);
        assertEquals("new_encoded_pass", mockUser.getPassword());
    }

    @Test
    public void testVerifyEmailSuccess() {
        VerifyEmailRequest verifyRequest = new VerifyEmailRequest("verify@example.com", "token_123");
        User mockUser = new User("verifyuser", "pass", "verify@example.com");

        when(userRepository.findByEmail("verify@example.com")).thenReturn(Optional.of(mockUser));

        authService.verifyEmail(verifyRequest);

        verify(userRepository).save(mockUser);
        assertTrue(mockUser.isEmailVerified());
    }

    @Test
    public void testVerifyEmailUserNotFound() {
        VerifyEmailRequest verifyRequest = new VerifyEmailRequest("nonexistent@example.com", "token_123");
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> authService.verifyEmail(verifyRequest));
    }
}
