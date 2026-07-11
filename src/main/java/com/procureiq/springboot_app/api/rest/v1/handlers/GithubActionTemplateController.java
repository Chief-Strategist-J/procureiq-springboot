package com.procureiq.springboot_app.api.rest.v1.handlers;

import com.procureiq.springboot_app.features.githubactions.dto.GithubActionTemplateResponse;
import com.procureiq.springboot_app.features.githubactions.service.GithubActionTemplateService;
import com.procureiq.springboot_app.shared.types.ApiResponse;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/github/templates")
public class GithubActionTemplateController {

    private final GithubActionTemplateService githubActionTemplateService;
    private final Tracer tracer = GlobalOpenTelemetry.getTracer("springboot-app", "1.0.0");

    public GithubActionTemplateController(GithubActionTemplateService githubActionTemplateService) {
        this.githubActionTemplateService = githubActionTemplateService;
    }

    @GetMapping
    public ResponseEntity<?> getAllTemplates() {
        Span span = tracer.spanBuilder("REST.getAllGithubActionTemplates").startSpan();
        try (Scope scope = span.makeCurrent()) {
            List<GithubActionTemplateResponse> response = githubActionTemplateService.getAllTemplates();
            span.setStatus(StatusCode.OK);
            return ResponseEntity.ok(ApiResponse.success(200, response));
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(500, e.getMessage()));
        } finally {
            span.end();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTemplate(@PathVariable Long id) {
        Span span = tracer.spanBuilder("REST.getGithubActionTemplate").startSpan();
        try (Scope scope = span.makeCurrent()) {
            GithubActionTemplateResponse response = githubActionTemplateService.getTemplate(id);
            span.setStatus(StatusCode.OK);
            return ResponseEntity.ok(ApiResponse.success(200, response));
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(400, e.getMessage()));
        } finally {
            span.end();
        }
    }
}
