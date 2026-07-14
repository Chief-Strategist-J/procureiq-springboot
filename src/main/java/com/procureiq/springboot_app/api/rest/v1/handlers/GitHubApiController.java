package com.procureiq.springboot_app.api.rest.v1.handlers;

import com.procureiq.springboot_app.infra.adapters.GitHubApiService;
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
    public ResponseEntity<?> triggerDispatch(@jakarta.validation.Valid @RequestBody DispatchRequest request) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
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
            return ResponseEntity.status(HttpStatus.OK).body(ApiSingleResponse.success(200, "Repository dispatch triggered successfully"));
        });
    }

    @GetMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.REPO_INFO)
    public ResponseEntity<?> getRepoInfo(@RequestParam String owner, @RequestParam String repo) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            if (owner == null || owner.trim().isEmpty()) {
                throw new IllegalArgumentException("Repository owner is required");
            }
            if (repo == null || repo.trim().isEmpty()) {
                throw new IllegalArgumentException("Repository name is required");
            }
            Map<String, Object> details = gitHubApiService.getRepositoryDetails(owner, repo);
            return ResponseEntity.ok(ApiSingleResponse.success(200, details));
        });
    }

    @GetMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.WORKFLOW_RUNS)
    public ResponseEntity<?> getWorkflowRuns(@RequestParam String owner, @RequestParam String repo) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            if (owner == null || owner.trim().isEmpty()) {
                throw new IllegalArgumentException("Repository owner is required");
            }
            if (repo == null || repo.trim().isEmpty()) {
                throw new IllegalArgumentException("Repository name is required");
            }
            List<Map<String, Object>> runs = gitHubApiService.getWorkflowRuns(owner, repo);
            return ResponseEntity.ok(ApiListResponse.success(200, runs));
        });
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
    public ResponseEntity<?> createWorkflow(@jakarta.validation.Valid @RequestBody CreateWorkflowRequest request) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
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

            return ResponseEntity.status(HttpStatus.CREATED).body(ApiSingleResponse.success(201, result));
        });
    }

    public record DeleteWorkflowRequest(
        String owner, String repo, String workflowName, String commitMessage
    ) {}

    /** Deletes an existing .github/workflows/{workflowName}.yml from the repository. */
    @DeleteMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.DELETE_WORKFLOW)
    public ResponseEntity<?> deleteWorkflow(@jakarta.validation.Valid @RequestBody DeleteWorkflowRequest request) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
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

            return ResponseEntity.ok(ApiSingleResponse.success(200, "Workflow deleted successfully"));
        });
    }
}

