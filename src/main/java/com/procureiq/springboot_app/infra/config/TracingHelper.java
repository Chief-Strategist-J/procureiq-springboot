package com.procureiq.springboot_app.infra.config;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.procureiq.springboot_app.shared.types.ApiResponse;
import java.util.concurrent.Callable;

public final class TracingHelper {
    private static final Tracer tracer = GlobalOpenTelemetry.getTracer("springboot-app", "1.0.0");

    private TracingHelper() {}

    public static ResponseEntity<?> executeWithTracing(Callable<ResponseEntity<?>> block) {
        String spanName = "REST.Controller";
        try {
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            if (stackTrace.length > 2) {
                StackTraceElement caller = stackTrace[2];
                String className = caller.getClassName();
                String simpleName = className.substring(className.lastIndexOf('.') + 1);
                spanName = "REST." + simpleName + "." + caller.getMethodName();
            }
        } catch (Exception ignored) {}

        Span span = tracer.spanBuilder(spanName).startSpan();
        try (Scope scope = span.makeCurrent()) {
            ResponseEntity<?> response = block.call();
            span.setStatus(StatusCode.OK);
            return response;
        } catch (Exception e) {
            span.recordException(e);
            span.setStatus(StatusCode.ERROR, e.getMessage());
            HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

            if (e instanceof com.procureiq.springboot_app.shared.exceptions.ResourceNotFoundException) {
                status = HttpStatus.NOT_FOUND;
            } else if (e instanceof com.procureiq.springboot_app.shared.exceptions.UnauthorizedException) {
                status = HttpStatus.UNAUTHORIZED;
            } else if (e instanceof com.procureiq.springboot_app.shared.exceptions.ForbiddenException) {
                status = HttpStatus.FORBIDDEN;
            } else if (e instanceof IllegalArgumentException || e instanceof IllegalStateException) {
                if (e.getMessage() != null && e.getMessage().toLowerCase().contains("not found")) {
                    status = HttpStatus.NOT_FOUND;
                } else {
                    status = HttpStatus.BAD_REQUEST;
                }
            }

            return ResponseEntity.status(status)
                    .body(ApiResponse.error(status.value(), e.getMessage()));
        } finally {
            span.end();
        }
    }
}
