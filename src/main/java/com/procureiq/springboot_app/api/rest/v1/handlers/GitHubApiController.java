package com.procureiq.springboot_app.api.rest.v1.handlers;

import com.procureiq.springboot_app.infra.adapters.GitHubApiService;
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
import java.util.Map;

@RestController
@RequestMapping("/api/v1/github")
public class GitHubApiController {

    private final GitHubApiService gitHubApiService;
    private final Tracer tracer = GlobalOpenTelemetry.getTracer("springboot-app", "1.0.0");

    public GitHubApiController(GitHubApiService gitHubApiService) {
        this.gitHubApiService = gitHubApiService;
    }

    public record DispatchRequest(String owner, String repo, String eventType, Map<String, Object> clientPayload) {}

    @PostMapping("/dispatch")
    public ResponseEntity<?> triggerDispatch(@RequestBody DispatchRequest request) {
        Span span = tracer.spanBuilder("REST.triggerDispatch").startSpan();
        try (Scope scope = span.makeCurrent()) {
            if (request.owner() == null || request.owner().trim().isEmpty()) {
                throw new IllegalArgumentException("Repository owner is required");
            }
            if (request.repo() == null || request.repo().trim().isEmpty()) {
                throw new IllegalArgumentException("Repository name is required");
            }
            if (request.eventType() == null || request.eventType().trim().isEmpty()) {
                throw new IllegalArgumentException("Event type is required");
            }
            gitHubApiService.triggerRepositoryDispatch(request.owner(), request.repo(), request.eventType(), request.clientPayload());
            span.setStatus(StatusCode.OK);
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(200, "Repository dispatch triggered successfully"));
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(400, e.getMessage()));
        } finally {
            span.end();
        }
    }

    @GetMapping("/repo-info")
    public ResponseEntity<?> getRepoInfo(@RequestParam String owner, @RequestParam String repo) {
        Span span = tracer.spanBuilder("REST.getRepoInfo").startSpan();
        try (Scope scope = span.makeCurrent()) {
            if (owner == null || owner.trim().isEmpty()) {
                throw new IllegalArgumentException("Repository owner is required");
            }
            if (repo == null || repo.trim().isEmpty()) {
                throw new IllegalArgumentException("Repository name is required");
            }
            Map<String, Object> details = gitHubApiService.getRepositoryDetails(owner, repo);
            span.setStatus(StatusCode.OK);
            return ResponseEntity.ok(ApiResponse.success(200, details));
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(400, e.getMessage()));
        } finally {
            span.end();
        }
    }

    @GetMapping("/workflow-runs")
    public ResponseEntity<?> getWorkflowRuns(@RequestParam String owner, @RequestParam String repo) {
        Span span = tracer.spanBuilder("REST.getWorkflowRuns").startSpan();
        try (Scope scope = span.makeCurrent()) {
            if (owner == null || owner.trim().isEmpty()) {
                throw new IllegalArgumentException("Repository owner is required");
            }
            if (repo == null || repo.trim().isEmpty()) {
                throw new IllegalArgumentException("Repository name is required");
            }
            List<Map<String, Object>> runs = gitHubApiService.getWorkflowRuns(owner, repo);
            span.setStatus(StatusCode.OK);
            return ResponseEntity.ok(ApiResponse.success(200, runs));
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(400, e.getMessage()));
        } finally {
            span.end();
        }
    }
}
