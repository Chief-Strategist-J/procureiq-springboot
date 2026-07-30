package com.procureiq.springboot_app.features.auth.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.procureiq.springboot_app.features.auth.dto.request.*;
import com.procureiq.springboot_app.features.auth.dto.response.*;
import com.procureiq.springboot_app.features.auth.entity.User;
import com.procureiq.springboot_app.features.auth.repository.UserRepository;
import com.procureiq.springboot_app.infra.config.TracingHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;
import java.time.LocalDateTime;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Algorithm jwtAlgorithm;
    private final long jwtExpirationMs;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            com.procureiq.springboot_app.infra.config.AppProperties appProperties) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtAlgorithm = Algorithm.HMAC256(appProperties.getJwtSecret());
        this.jwtExpirationMs = appProperties.getJwtExpirationMs();
    }

    public UserResponse signup(SignupRequest request) {
        return TracingHelper.executeServiceWithTracing(() -> {
            if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
                throw new IllegalArgumentException("Username cannot be empty");
            }
            if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
                throw new IllegalArgumentException("Email cannot be empty");
            }
            if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
                throw new IllegalArgumentException("Password cannot be empty");
            }

            if (userRepository.existsByUsername(request.getUsername())) {
                throw new IllegalArgumentException("Username already exists");
            }
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new IllegalArgumentException("Email already exists");
            }

            User user = new User(
                request.getUsername(),
                passwordEncoder.encode(request.getPassword()),
                request.getEmail()
            );

            User savedUser = userRepository.save(user);
            return new UserResponse(savedUser.getId(), savedUser.getUsername(), savedUser.getEmail());
        });
    }

    public LoginResponse login(LoginRequest request) {
        return TracingHelper.executeServiceWithTracing(() -> {
            if (request.getUsername() == null || request.getUsername().trim().isEmpty() ||
                request.getPassword() == null || request.getPassword().trim().isEmpty()) {
                throw new IllegalArgumentException("Username and password cannot be empty");
            }

            User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new com.procureiq.springboot_app.shared.exceptions.UnauthorizedException("Invalid username or password"));

            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                throw new com.procureiq.springboot_app.shared.exceptions.UnauthorizedException("Invalid username or password");
            }

            String token = JWT.create()
                .withIssuer("procureiq")
                .withSubject(user.getUsername())
                .withClaim("email", user.getEmail())
                .withExpiresAt(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .sign(jwtAlgorithm);

            UserResponse userResponse = new UserResponse(user.getId(), user.getUsername(), user.getEmail());
            return new LoginResponse(token, userResponse);
        });
    }

    public void forgotPassword(ForgotPasswordRequest request) {
        TracingHelper.executeServiceVoidWithTracing(() -> {
            if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
                throw new IllegalArgumentException("Email cannot be empty");
            }

            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new IllegalArgumentException("If email exists, a reset token will be generated."));

            String token = UUID.randomUUID().toString();
            user.setResetToken(token);
            user.setResetTokenExpiry(LocalDateTime.now().plusHours(1));
            userRepository.save(user);

            System.out.printf("[DEV ONLY] Password reset token for %s: %s%n", user.getEmail(), token);
        });
    }

    public void resetPassword(ResetPasswordRequest request) {
        TracingHelper.executeServiceVoidWithTracing(() -> {
            if (request.getToken() == null || request.getToken().trim().isEmpty()) {
                throw new IllegalArgumentException("Token cannot be empty");
            }
            if (request.getNewPassword() == null || request.getNewPassword().trim().isEmpty()) {
                throw new IllegalArgumentException("New password cannot be empty");
            }

            User user = userRepository.findByResetToken(request.getToken())
                    .orElseThrow(() -> new com.procureiq.springboot_app.shared.exceptions.UnauthorizedException("Invalid or expired reset token"));

            if (user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
                throw new com.procureiq.springboot_app.shared.exceptions.UnauthorizedException("Invalid or expired reset token");
            }

            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            user.setResetToken("");
            user.setResetTokenExpiry(LocalDateTime.of(1970, 1, 1, 0, 0));
            userRepository.save(user);
        });
    }

    public void verifyEmail(VerifyEmailRequest request) {
        TracingHelper.executeServiceVoidWithTracing(() -> {
            if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
                throw new IllegalArgumentException("Email cannot be empty");
            }
            if (request.getToken() == null || request.getToken().trim().isEmpty()) {
                throw new IllegalArgumentException("Verification token cannot be empty");
            }

            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new IllegalArgumentException("User not found for provided email"));

            user.setEmailVerified(true);
            user.setVerificationToken("");
            userRepository.save(user);
        });
    }
}
