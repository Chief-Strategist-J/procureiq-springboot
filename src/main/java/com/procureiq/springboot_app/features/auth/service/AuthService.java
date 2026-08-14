package com.procureiq.springboot_app.features.auth.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.procureiq.springboot_app.features.auth.dto.request.*;
import com.procureiq.springboot_app.features.auth.dto.response.*;
import com.procureiq.springboot_app.features.auth.entity.User;
import com.procureiq.springboot_app.features.auth.repository.UserRepository;
import com.procureiq.springboot_app.infra.config.TracingHelper;
import com.procureiq.springboot_app.shared.exceptions.UnauthorizedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

import com.procureiq.springboot_app.shared.ports.NotificationSender;
import com.procureiq.springboot_app.shared.exceptions.ResourceNotFoundException;

@Service
public class AuthService {

    private static final int MAX_FAILED_ATTEMPTS = 3;
    private static final long LOCK_TIME_DURATION_MINUTES = 15;
    private static final long REFRESH_TOKEN_EXPIRATION_DAYS = 7;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Algorithm jwtAlgorithm;
    private final long jwtExpirationMs;
    private final NotificationSender notificationSender;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            com.procureiq.springboot_app.infra.config.AppProperties appProperties,
            NotificationSender notificationSender) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtAlgorithm = Algorithm.HMAC256(appProperties.getJwtSecret());
        this.jwtExpirationMs = appProperties.getJwtExpirationMs();
        this.notificationSender = notificationSender;
    }

    public SignupResponse signup(SignupRequest request) {
        return TracingHelper.executeServiceWithTracing(() -> {
            if (com.procureiq.springboot_app.shared.utils.StringUtils.isEmpty(request.getUsername())) {
                throw new IllegalArgumentException("Username cannot be empty");
            }
            if (com.procureiq.springboot_app.shared.utils.StringUtils.isEmpty(request.getEmail())) {
                throw new IllegalArgumentException("Email cannot be empty");
            }
            if (com.procureiq.springboot_app.shared.utils.StringUtils.isEmpty(request.getPassword())) {
                throw new IllegalArgumentException("Password cannot be empty");
            }

            java.util.Optional<User> existingUserOpt = userRepository.findByEmail(request.getEmail());
            if (existingUserOpt.isPresent()) {
                User existingUser = existingUserOpt.get();
                if (passwordEncoder.matches(request.getPassword(), existingUser.getPassword())) {
                    String accessToken = generateAccessToken(existingUser);
                    UserResponse userResponse = new UserResponse(existingUser.getId(), existingUser.getUsername(), existingUser.getEmail(), existingUser.getRole());
                    return new SignupResponse(userResponse, accessToken, true);
                } else {
                    throw new com.procureiq.springboot_app.shared.exceptions.UserAlreadyExistsException("An account with this email address already exists. Please sign in with your credentials.");
                }
            }

            User user = new User(
                request.getUsername(),
                passwordEncoder.encode(request.getPassword()),
                request.getEmail(),
                "user"
            );

            User savedUser = userRepository.save(user);
            String accessToken = generateAccessToken(savedUser);
            UserResponse userResponse = new UserResponse(savedUser.getId(), savedUser.getUsername(), savedUser.getEmail(), savedUser.getRole());
            return new SignupResponse(userResponse, accessToken, true);
        });
    }

    public LoginResponse login(LoginRequest request) {
        return TracingHelper.executeServiceWithTracing(() -> {
            String identifier = request.getUsername() != null ? request.getUsername().trim() : "";
            String password = request.getPassword() != null ? request.getPassword().trim() : "";

            if (identifier.isEmpty() || password.isEmpty()) {
                throw new IllegalArgumentException("Username/email and password cannot be empty");
            }

            User user = userRepository.findByUsernameOrEmail(identifier, identifier)
                .orElseThrow(() -> new UnauthorizedException("Invalid username or password"));

            if (!user.isAccountNonLocked()) {
                if (user.getLockTime() != null && user.getLockTime().plusMinutes(LOCK_TIME_DURATION_MINUTES).isBefore(LocalDateTime.now())) {
                    user.setAccountNonLocked(true);
                    user.setFailedAttemptCount(0);
                    user.setLockTime(null);
                } else {
                    throw new UnauthorizedException("Account is temporarily locked due to multiple failed login attempts. Please try again later.");
                }
            }

            if (!passwordEncoder.matches(password, user.getPassword())) {
                int attempts = user.getFailedAttemptCount() + 1;
                user.setFailedAttemptCount(attempts);
                if (attempts >= MAX_FAILED_ATTEMPTS) {
                    user.setAccountNonLocked(false);
                    user.setLockTime(LocalDateTime.now());
                }
                userRepository.save(user);
                throw new UnauthorizedException("Invalid username or password");
            }

            user.setFailedAttemptCount(0);
            user.setAccountNonLocked(true);
            user.setLockTime(null);

            String accessToken = generateAccessToken(user);
            String refreshToken = UUID.randomUUID().toString();

            user.setRefreshToken(refreshToken);
            user.setRefreshTokenExpiry(LocalDateTime.now().plusDays(REFRESH_TOKEN_EXPIRATION_DAYS));
            userRepository.save(user);

            UserResponse userResponse = new UserResponse(user.getId(), user.getUsername(), user.getEmail(), user.getRole());
            return new LoginResponse(accessToken, refreshToken, userResponse);
        });
    }

    public RefreshTokenResponse refreshToken(RefreshTokenRequest request) {
        return TracingHelper.executeServiceWithTracing(() -> {
            String token = request.getRefreshToken() != null ? request.getRefreshToken().trim() : "";
            if (token.isEmpty()) {
                throw new IllegalArgumentException("Refresh token cannot be empty");
            }

            User user = userRepository.findByRefreshToken(token)
                .orElseThrow(() -> new UnauthorizedException("Invalid or revoked refresh token"));

            if (user.getRefreshTokenExpiry().isBefore(LocalDateTime.now())) {
                user.setRefreshToken("");
                userRepository.save(user);
                throw new UnauthorizedException("Refresh token has expired. Please login again.");
            }

            String newAccessToken = generateAccessToken(user);
            String newRefreshToken = UUID.randomUUID().toString();

            user.setRefreshToken(newRefreshToken);
            user.setRefreshTokenExpiry(LocalDateTime.now().plusDays(REFRESH_TOKEN_EXPIRATION_DAYS));
            userRepository.save(user);

            return new RefreshTokenResponse(newAccessToken, newRefreshToken);
        });
    }

    public void logout(String refreshToken) {
        TracingHelper.executeServiceVoidWithTracing(() -> {
            if (refreshToken != null && !refreshToken.trim().isEmpty()) {
                userRepository.findByRefreshToken(refreshToken.trim()).ifPresent(user -> {
                    user.setRefreshToken("");
                    user.setRefreshTokenExpiry(LocalDateTime.of(1970, 1, 1, 0, 0));
                    userRepository.save(user);
                });
            }
        });
    }

    public void forgotPassword(ForgotPasswordRequest request) {
        TracingHelper.executeServiceVoidWithTracing(() -> {
            if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
                throw new IllegalArgumentException("Email cannot be empty");
            }

            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + request.getEmail()));

            String token = UUID.randomUUID().toString();
            user.setResetToken(token);
            user.setResetTokenExpiry(LocalDateTime.now().plusHours(1));
            userRepository.save(user);

            String resetLink = "http://localhost:3000/reset-password?token=" + token;
            String emailBody = "Hello,\n\nYou requested to reset your password. Click the link below to reset it:\n"
                    + resetLink + "\n\nThis link will expire in 1 hour.\n\nBest regards,\nProcureIQ Team";
            
            try {
                notificationSender.send("email", "smtp", user.getEmail(), "Password Reset Request", emailBody);
            } catch (Exception e) {
                user.setResetToken("");
                user.setResetTokenExpiry(LocalDateTime.of(1970, 1, 1, 0, 0));
                userRepository.save(user);
                throw new IllegalStateException("Failed to send password reset email via SMTP", e);
            }
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
                    .orElseThrow(() -> new UnauthorizedException("Invalid or expired reset token"));

            if (user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
                throw new UnauthorizedException("Invalid or expired reset token");
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

    private String generateAccessToken(User user) {
        return JWT.create()
            .withIssuer("procureiq")
            .withSubject(user.getUsername())
            .withClaim("email", user.getEmail())
            .withClaim("role", user.getRole())
            .withExpiresAt(new Date(System.currentTimeMillis() + jwtExpirationMs))
            .sign(jwtAlgorithm);
    }
}
