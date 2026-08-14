package com.procureiq.springboot_app.api.rest.v1.handlers;

import com.procureiq.springboot_app.features.auth.dto.request.*;
import com.procureiq.springboot_app.features.auth.dto.response.*;
import com.procureiq.springboot_app.features.auth.service.AuthService;
import com.procureiq.springboot_app.shared.types.single.ApiSingleResponse;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Tracer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.AUTH)
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;
    private final Tracer tracer = GlobalOpenTelemetry.getTracer("springboot-app", "1.0.0");

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.SIGNUP)
    public ResponseEntity<?> signup(@jakarta.validation.Valid @RequestBody SignupRequest request) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            SignupResponse response = authService.signup(request);
            HttpStatus status = response.isAutoLogin() ? HttpStatus.OK : HttpStatus.CREATED;
            int code = response.isAutoLogin() ? 200 : 201;
            return ResponseEntity.status(status).body(ApiSingleResponse.success(code, response));
        });
    }

    @PostMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.LOGIN)
    public ResponseEntity<?> login(@jakarta.validation.Valid @RequestBody LoginRequest request) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            LoginResponse response = authService.login(request);
            return ResponseEntity.ok(ApiSingleResponse.success(200, response));
        });
    }

    @PostMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.REFRESH_TOKEN)
    public ResponseEntity<?> refreshToken(@jakarta.validation.Valid @RequestBody RefreshTokenRequest request) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            RefreshTokenResponse response = authService.refreshToken(request);
            return ResponseEntity.ok(ApiSingleResponse.success(200, response));
        });
    }

    @PostMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.LOGOUT)
    public ResponseEntity<?> logout(@RequestBody(required = false) RefreshTokenRequest request) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            String token = request != null ? request.getRefreshToken() : "";
            authService.logout(token);
            return ResponseEntity.ok(ApiSingleResponse.success(200, "Logout successful. Session invalidated."));
        });
    }

    @PostMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.FORGOT_PASSWORD)
    public ResponseEntity<?> forgotPassword(@jakarta.validation.Valid @RequestBody ForgotPasswordRequest request) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            authService.forgotPassword(request);
            return ResponseEntity.ok(ApiSingleResponse.success(200, "If the email matches an active account, a reset token has been generated."));
        });
    }

    @PostMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.RESET_PASSWORD)
    public ResponseEntity<?> resetPassword(@jakarta.validation.Valid @RequestBody ResetPasswordRequest request) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            authService.resetPassword(request);
            return ResponseEntity.ok(ApiSingleResponse.success(200, "Password has been reset successfully."));
        });
    }

    @PostMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.VERIFY_EMAIL)
    public ResponseEntity<?> verifyEmail(@jakarta.validation.Valid @RequestBody VerifyEmailRequest request) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            authService.verifyEmail(request);
            return ResponseEntity.ok(ApiSingleResponse.success(200, "Email has been verified successfully."));
        });
    }
}
