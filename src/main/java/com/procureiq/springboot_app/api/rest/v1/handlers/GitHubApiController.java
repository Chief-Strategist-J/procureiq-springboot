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
@RequestMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.GITHUB)
public class GitHubApiController {

    private final GitHubApiService gitHubApiService;
    private final Tracer tracer = GlobalOpenTelemetry.getTracer("springboot-app", "1.0.0");

    public GitHubApiController(GitHubApiService gitHubApiService) {
        this.gitHubApiService = gitHubApiService;
    }

    public record DispatchRequest(String owner, String repo, String eventType, Map<String, Object> clientPayload) {}

    @PostMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.DISPATCH)
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

    @GetMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.REPO_INFO)
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

    @GetMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.WORKFLOW_RUNS)
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

    public record CreateWorkflowRequest(
        String owner,
        String repo,
        String workflowName,
        String yamlContent,
        String commitMessage
    ) {}

    /**
     * Creates or updates a GitHub Actions workflow file at
     * .github/workflows/{workflowName}.yml inside the target repository.
     * In mock mode (no GITHUB_TOKEN), the file is stored in-memory and
     * a mock response is returned — no real GitHub call is made.
     */
    @PostMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.CREATE_WORKFLOW)
    public ResponseEntity<?> createWorkflow(@RequestBody CreateWorkflowRequest request) {
        Span span = tracer.spanBuilder("REST.createWorkflow").startSpan();
        try (Scope scope = span.makeCurrent()) {
            if (request.owner() == null || request.owner().trim().isEmpty())
                throw new IllegalArgumentException("Repository owner is required");
            if (request.repo() == null || request.repo().trim().isEmpty())
                throw new IllegalArgumentException("Repository name is required");
            if (request.workflowName() == null || request.workflowName().trim().isEmpty())
                throw new IllegalArgumentException("Workflow name is required");
            if (request.yamlContent() == null || request.yamlContent().trim().isEmpty())
                throw new IllegalArgumentException("Workflow YAML content is required");

            String msg = request.commitMessage() != null && !request.commitMessage().isBlank()
                    ? request.commitMessage()
                    : "ci: add " + request.workflowName() + " workflow via ProcureIQ";

            Map<String, Object> result = gitHubApiService.createWorkflowFile(
                    request.owner(), request.repo(),
                    request.workflowName(), request.yamlContent(), msg);

            span.setStatus(StatusCode.OK);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(201, result));
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(400, e.getMessage()));
        } finally {
            span.end();
        }
    }

    public record DeleteWorkflowRequest(
        String owner, String repo, String workflowName, String commitMessage
    ) {}

    /** Deletes an existing .github/workflows/{workflowName}.yml from the repository. */
    @DeleteMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.DELETE_WORKFLOW)
    public ResponseEntity<?> deleteWorkflow(@RequestBody DeleteWorkflowRequest request) {
        Span span = tracer.spanBuilder("REST.deleteWorkflow").startSpan();
        try (Scope scope = span.makeCurrent()) {
            if (request.owner() == null || request.owner().trim().isEmpty())
                throw new IllegalArgumentException("Repository owner is required");
            if (request.repo() == null || request.repo().trim().isEmpty())
                throw new IllegalArgumentException("Repository name is required");
            if (request.workflowName() == null || request.workflowName().trim().isEmpty())
                throw new IllegalArgumentException("Workflow name is required");

            String msg = request.commitMessage() != null && !request.commitMessage().isBlank()
                    ? request.commitMessage()
                    : "ci: remove " + request.workflowName() + " workflow via ProcureIQ";

            gitHubApiService.deleteWorkflowFile(
                    request.owner(), request.repo(), request.workflowName(), msg);

            span.setStatus(StatusCode.OK);
            return ResponseEntity.ok(ApiResponse.success(200, "Workflow deleted successfully"));
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(400, e.getMessage()));
        } finally {
            span.end();
        }
    }
}

