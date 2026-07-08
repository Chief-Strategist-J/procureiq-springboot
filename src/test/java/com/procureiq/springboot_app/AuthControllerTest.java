package com.procureiq.springboot_app;

import tools.jackson.databind.json.JsonMapper;
import com.procureiq.springboot_app.features.auth.dto.*;
import com.procureiq.springboot_app.features.auth.entity.User;
import com.procureiq.springboot_app.features.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    private final JsonMapper objectMapper = new JsonMapper();

    @BeforeEach
    public void setup() {
        userRepository.deleteAll();
    }

    @Test
    public void testSignupSuccess() throws Exception {
        SignupRequest request = new SignupRequest("devuser", "password123", "dev@example.com");

        mockMvc.perform(post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.username", is("devuser")))
                .andExpect(jsonPath("$.email", is("dev@example.com")));
    }

    @Test
    public void testSignupDuplicateUsername() throws Exception {
        SignupRequest request1 = new SignupRequest("devuser", "password123", "dev1@example.com");
        mockMvc.perform(post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated());

        SignupRequest request2 = new SignupRequest("devuser", "password456", "dev2@example.com");
        mockMvc.perform(post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testLoginSuccess() throws Exception {
        SignupRequest signup = new SignupRequest("loginuser", "password123", "login@example.com");
        mockMvc.perform(post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signup)))
                .andExpect(status().isCreated());

        LoginRequest login = new LoginRequest("loginuser", "password123");
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.user.username", is("loginuser")));
    }

    @Test
    public void testLoginInvalidCredentials() throws Exception {
        LoginRequest login = new LoginRequest("nonexistent", "wrongpass");
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testForgotPasswordAndResetPasswordSuccess() throws Exception {
        SignupRequest signup = new SignupRequest("resetuser", "password123", "reset@example.com");
        mockMvc.perform(post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signup)))
                .andExpect(status().isCreated());

        // Trigger forgot password
        ForgotPasswordRequest forgot = new ForgotPasswordRequest("reset@example.com");
        mockMvc.perform(post("/api/v1/auth/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(forgot)))
                .andExpect(status().isOk());

        // Verify token saved in DB
        User user = userRepository.findByEmail("reset@example.com").orElseThrow();
        String token = user.getResetToken();
        assertNotNull(token);
        assertFalse(token.isEmpty());

        // Reset password
        ResetPasswordRequest reset = new ResetPasswordRequest(token, "newpassword123");
        mockMvc.perform(post("/api/v1/auth/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reset)))
                .andExpect(status().isOk());

        // Login with new password
        LoginRequest login = new LoginRequest("resetuser", "newpassword123");
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk());
    }

    @Test
    public void testResetPasswordExpiredToken() throws Exception {
        SignupRequest signup = new SignupRequest("expireduser", "password123", "expired@example.com");
        mockMvc.perform(post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signup)))
                .andExpect(status().isCreated());

        User user = userRepository.findByEmail("expired@example.com").orElseThrow();
        user.setResetToken("expired-token-xyz");
        user.setResetTokenExpiry(LocalDateTime.now().minusMinutes(5)); // already expired
        userRepository.save(user);

        ResetPasswordRequest reset = new ResetPasswordRequest("expired-token-xyz", "newpassword123");
        mockMvc.perform(post("/api/v1/auth/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reset)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testSignupNullAndEmptyValues() throws Exception {
        SignupRequest request = new SignupRequest("", "", "");
        mockMvc.perform(post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testForgotPasswordNonExistentEmail() throws Exception {
        ForgotPasswordRequest forgot = new ForgotPasswordRequest("nonexistent@example.com");
        // Should return 200 OK to prevent user enumeration
        mockMvc.perform(post("/api/v1/auth/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(forgot)))
                .andExpect(status().isOk());
    }

    @Test
    public void testResetPasswordInvalidToken() throws Exception {
        ResetPasswordRequest reset = new ResetPasswordRequest("nonexistent-token-12345", "newpassword123");
        mockMvc.perform(post("/api/v1/auth/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reset)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testResetPasswordBlankNewPassword() throws Exception {
        ResetPasswordRequest reset = new ResetPasswordRequest("some-token", "");
        mockMvc.perform(post("/api/v1/auth/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reset)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testSQLInjectionAttemptInLogin() throws Exception {
        LoginRequest login = new LoginRequest("' OR '1'='1", "' OR '1'='1");
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isUnauthorized());
    }
}
