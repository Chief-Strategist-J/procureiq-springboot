package com.procureiq.springboot_app.api.rest.v1.handlers;

import com.procureiq.springboot_app.features.githubactions.dto.response.GithubActionTemplateResponse;
import com.procureiq.springboot_app.features.githubactions.service.GithubActionTemplateService;
import com.procureiq.springboot_app.shared.types.single.ApiSingleResponse;
import com.procureiq.springboot_app.shared.types.list.ApiListResponse;
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
@RequestMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.GITHUB_TEMPLATES)
public class GithubActionTemplateController {

    private final GithubActionTemplateService githubActionTemplateService;
    private final Tracer tracer = GlobalOpenTelemetry.getTracer("springboot-app", "1.0.0");

    public GithubActionTemplateController(GithubActionTemplateService githubActionTemplateService) {
        this.githubActionTemplateService = githubActionTemplateService;
    }

    @GetMapping
    public ResponseEntity<?> getAllTemplates() {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            List<GithubActionTemplateResponse> response = githubActionTemplateService.getAllTemplates();
            return ResponseEntity.ok(ApiListResponse.success(200, response));
        });
    }

    @GetMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.PATH_ID)
    public ResponseEntity<?> getTemplate(@PathVariable Long id) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            GithubActionTemplateResponse response = githubActionTemplateService.getTemplate(id);
            return ResponseEntity.ok(ApiSingleResponse.success(200, response));
        });
    }
}
