package com.procureiq.springboot_app.infra.config;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.procureiq.springboot_app.shared.types.single.ApiSingleResponse;
import java.util.concurrent.Callable;

public final class TracingHelper {
    private static final Tracer tracer = GlobalOpenTelemetry.getTracer("springboot-app", "1.0.0");

    private TracingHelper() {}

    @FunctionalInterface
    public interface ServiceBlock<T> {
        T execute() throws Exception;
    }

    @FunctionalInterface
    public interface ServiceVoidBlock {
        void execute() throws Exception;
    }

    public static ResponseEntity<?> executeWithTracing(final Callable<ResponseEntity<?>> block) {
        String spanName = "REST.Controller";
        try {
            final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            if (stackTrace.length > 2) {
                final StackTraceElement caller = stackTrace[2];
                final String className = caller.getClassName();
                final String simpleName = className.substring(className.lastIndexOf('.') + 1);
                spanName = "REST." + simpleName + "." + caller.getMethodName();
            }
        } catch (final Exception ignored) {}

        final Span span = tracer.spanBuilder(spanName).startSpan();
        try (final Scope scope = span.makeCurrent()) {
            final ResponseEntity<?> response = block.call();
            span.setStatus(StatusCode.OK);
            return response;
        } catch (final Exception e) {
            span.recordException(e);
            span.setStatus(StatusCode.ERROR, e.getMessage());
            HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

            String rawMsg = e.getMessage() != null ? e.getMessage() : "";
            String userMsg = rawMsg;

            if (e instanceof org.springframework.web.bind.MethodArgumentNotValidException) {
                status = HttpStatus.BAD_REQUEST;
                final String validationMsg = ((org.springframework.web.bind.MethodArgumentNotValidException) e)
                        .getBindingResult().getFieldErrors().stream()
                        .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                        .collect(java.util.stream.Collectors.joining(", "));
                return ResponseEntity.status(status)
                        .body(ApiSingleResponse.error(status.value(), "Validation failed: " + validationMsg));
            }

            if (e instanceof com.procureiq.springboot_app.shared.exceptions.UserAlreadyExistsException) {
                status = HttpStatus.CONFLICT;
                userMsg = rawMsg.isBlank() ? "User already exists with this email address or username" : rawMsg;
            } else if (e instanceof org.springframework.dao.DataIntegrityViolationException) {
                boolean isEmail = rawMsg.contains("uq_users_email") || rawMsg.contains("users_email_key") || rawMsg.contains("email");
                status = isEmail ? HttpStatus.CONFLICT : HttpStatus.BAD_REQUEST;
                userMsg = isEmail ? "An account with this email address already exists. Please sign in instead." : "Database constraint violation occurred";
            } else if (e instanceof org.springframework.security.authentication.BadCredentialsException) {
                status = HttpStatus.UNAUTHORIZED;
                userMsg = "Invalid username or password. Please check your credentials.";
            } else if (e instanceof com.procureiq.springboot_app.shared.exceptions.ResourceNotFoundException) {
                status = HttpStatus.NOT_FOUND;
            } else if (e instanceof com.procureiq.springboot_app.shared.exceptions.UnauthorizedException) {
                status = HttpStatus.UNAUTHORIZED;
            } else if (e instanceof com.procureiq.springboot_app.shared.exceptions.ForbiddenException) {
                status = HttpStatus.FORBIDDEN;
            } else if (e instanceof IllegalArgumentException || e instanceof IllegalStateException) {
                if (rawMsg.toLowerCase().contains("not found")) {
                    status = HttpStatus.NOT_FOUND;
                } else {
                    status = HttpStatus.BAD_REQUEST;
                }
            } else if (rawMsg.contains("JDBC Connection") || rawMsg.contains("database \"procureiq\" does not exist") || e instanceof org.springframework.transaction.CannotCreateTransactionException) {
                status = HttpStatus.SERVICE_UNAVAILABLE;
                userMsg = "Database connection failed. Please verify that the database container is running and healthy.";
            }

            return ResponseEntity.status(status)
                    .body(ApiSingleResponse.error(status.value(), userMsg));
        } finally {
            span.end();
        }
    }

    public static <T> T executeServiceWithTracing(final ServiceBlock<T> block) {
        String spanName = "Service.Execution";
        try {
            final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            if (stackTrace.length > 2) {
                final StackTraceElement caller = stackTrace[2];
                final String className = caller.getClassName();
                final String simpleName = className.substring(className.lastIndexOf('.') + 1);
                spanName = "Service." + simpleName + "." + caller.getMethodName();
            }
        } catch (final Exception ignored) {}

        final Span span = tracer.spanBuilder(spanName).startSpan();
        try (final Scope scope = span.makeCurrent()) {
            final T result = block.execute();
            span.setStatus(StatusCode.OK);
            return result;
        } catch (final RuntimeException e) {
            span.recordException(e);
            span.setStatus(StatusCode.ERROR, e.getMessage());
            throw e;
        } catch (final Exception e) {
            span.recordException(e);
            span.setStatus(StatusCode.ERROR, e.getMessage());
            throw new RuntimeException(e);
        } finally {
            span.end();
        }
    }

    public static void executeServiceVoidWithTracing(final ServiceVoidBlock block) {
        executeServiceWithTracing(() -> {
            block.execute();
            return null;
        });
    }
}
