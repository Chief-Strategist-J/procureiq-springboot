package com.procureiq.springboot_app.features.auth.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.procureiq.springboot_app.features.auth.dto.*;
import com.procureiq.springboot_app.features.auth.entity.User;
import com.procureiq.springboot_app.features.auth.repository.UserRepository;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import org.springframework.beans.factory.annotation.Value;
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
    private final Tracer tracer = GlobalOpenTelemetry.getTracer("springboot-app", "1.0.0");

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
        Span span = tracer.spanBuilder("AuthService.signup")
                .setAttribute("auth.username", request.getUsername())
                .setAttribute("auth.email", request.getEmail())
                .startSpan();

        try (Scope scope = span.makeCurrent()) {
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
                span.addEvent("signup_failed_username_exists");
                throw new IllegalArgumentException("Username already exists");
            }
            if (userRepository.existsByEmail(request.getEmail())) {
                span.addEvent("signup_failed_email_exists");
                throw new IllegalArgumentException("Email already exists");
            }

            User user = new User(
                request.getUsername(),
                passwordEncoder.encode(request.getPassword()),
                request.getEmail()
            );

            User savedUser = userRepository.save(user);
            span.addEvent("signup_success");
            span.setStatus(StatusCode.OK);
            return new UserResponse(savedUser.getId(), savedUser.getUsername(), savedUser.getEmail());
        } catch (Exception e) {
            span.recordException(e);
            span.setStatus(StatusCode.ERROR, e.getMessage());
            throw e;
        } finally {
            span.end();
        }
    }

    public LoginResponse login(LoginRequest request) {
        Span span = tracer.spanBuilder("AuthService.login")
                .setAttribute("auth.username", request.getUsername())
                .startSpan();

        try (Scope scope = span.makeCurrent()) {
            if (request.getUsername() == null || request.getUsername().trim().isEmpty() ||
                request.getPassword() == null || request.getPassword().trim().isEmpty()) {
                throw new IllegalArgumentException("Username and password cannot be empty");
            }

            User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> {
                    span.addEvent("login_failed_user_not_found");
                    return new IllegalArgumentException("Invalid username or password");
                });

            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                span.addEvent("login_failed_incorrect_password");
                throw new IllegalArgumentException("Invalid username or password");
            }

            String token = JWT.create()
                .withIssuer("procureiq")
                .withSubject(user.getUsername())
                .withClaim("email", user.getEmail())
                .withExpiresAt(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .sign(jwtAlgorithm);

            span.addEvent("login_success");
            span.setStatus(StatusCode.OK);

            UserResponse userResponse = new UserResponse(user.getId(), user.getUsername(), user.getEmail());
            return new LoginResponse(token, userResponse);
        } catch (Exception e) {
            span.recordException(e);
            span.setStatus(StatusCode.ERROR, e.getMessage());
            throw e;
        } finally {
            span.end();
        }
    }

    public void forgotPassword(ForgotPasswordRequest request) {
        Span span = tracer.spanBuilder("AuthService.forgotPassword")
                .setAttribute("auth.email", request.getEmail())
                .startSpan();

        try (Scope scope = span.makeCurrent()) {
            if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
                throw new IllegalArgumentException("Email cannot be empty");
            }

            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> {
                        span.addEvent("forgot_password_failed_email_not_found");
                        return new IllegalArgumentException("If email exists, a reset token will be generated.");
                    });

            String token = UUID.randomUUID().toString();
            user.setResetToken(token);
            user.setResetTokenExpiry(LocalDateTime.now().plusHours(1));
            userRepository.save(user);

            span.addEvent("forgot_password_token_generated");
            span.setStatus(StatusCode.OK);

            System.out.printf("[DEV ONLY] Password reset token for %s: %s%n", user.getEmail(), token);
        } catch (Exception e) {
            span.recordException(e);
            span.setStatus(StatusCode.ERROR, e.getMessage());
            // Safe message to prevent user enumeration
            System.out.println("Forgot password request completed.");
        } finally {
            span.end();
        }
    }

    public void resetPassword(ResetPasswordRequest request) {
        Span span = tracer.spanBuilder("AuthService.resetPassword")
                .startSpan();

        try (Scope scope = span.makeCurrent()) {
            if (request.getToken() == null || request.getToken().trim().isEmpty()) {
                throw new IllegalArgumentException("Token cannot be empty");
            }
            if (request.getNewPassword() == null || request.getNewPassword().trim().isEmpty()) {
                throw new IllegalArgumentException("New password cannot be empty");
            }

            User user = userRepository.findByResetToken(request.getToken())
                    .orElseThrow(() -> {
                        span.addEvent("reset_password_failed_invalid_token");
                        return new IllegalArgumentException("Invalid or expired reset token");
                    });

            if (user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
                span.addEvent("reset_password_failed_token_expired");
                throw new IllegalArgumentException("Invalid or expired reset token");
            }

            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            user.setResetToken("");
            user.setResetTokenExpiry(LocalDateTime.of(1970, 1, 1, 0, 0));
            userRepository.save(user);

            span.addEvent("reset_password_success");
            span.setStatus(StatusCode.OK);
        } catch (Exception e) {
            span.recordException(e);
            span.setStatus(StatusCode.ERROR, e.getMessage());
            throw e;
        } finally {
            span.end();
        }
    }
}
