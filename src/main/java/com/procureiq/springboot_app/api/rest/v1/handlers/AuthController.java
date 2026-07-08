package com.procureiq.springboot_app.api.rest.v1.handlers;

import com.procureiq.springboot_app.features.auth.dto.*;
import com.procureiq.springboot_app.features.auth.service.AuthService;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;
    private final Tracer tracer = GlobalOpenTelemetry.getTracer("springboot-app", "1.0.0");

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest request) {
        Span span = tracer.spanBuilder("REST.signup").startSpan();
        try (Scope scope = span.makeCurrent()) {
            UserResponse response = authService.signup(request);
            span.setStatus(StatusCode.OK);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } finally {
            span.end();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Span span = tracer.spanBuilder("REST.login").startSpan();
        try (Scope scope = span.makeCurrent()) {
            LoginResponse response = authService.login(request);
            span.setStatus(StatusCode.OK);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } finally {
            span.end();
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        Span span = tracer.spanBuilder("REST.forgot-password").startSpan();
        try (Scope scope = span.makeCurrent()) {
            authService.forgotPassword(request);
            span.setStatus(StatusCode.OK);
            return ResponseEntity.ok("If the email matches an active account, a reset token has been generated.");
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } finally {
            span.end();
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        Span span = tracer.spanBuilder("REST.reset-password").startSpan();
        try (Scope scope = span.makeCurrent()) {
            authService.resetPassword(request);
            span.setStatus(StatusCode.OK);
            return ResponseEntity.ok("Password has been reset successfully.");
        } catch (IllegalArgumentException e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } finally {
            span.end();
        }
    }
}
