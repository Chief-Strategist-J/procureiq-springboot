package com.procureiq.springboot_app.api.rest.v1.handlers;

import com.procureiq.springboot_app.features.jobs.dto.request.*;
import com.procureiq.springboot_app.features.jobs.dto.response.*;
import com.procureiq.springboot_app.features.jobs.service.JobService;
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
@RequestMapping
public class JobController {

    private final JobService jobService;
    private final Tracer tracer = GlobalOpenTelemetry.getTracer("springboot-app", "1.0.0");

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    

    @GetMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.JOBS)
    public ResponseEntity<?> getAllJobs() {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            List<JobResponse> response = jobService.getAllJobs();
            return ResponseEntity.ok(ApiListResponse.success(200, response));
        });
    }

    @PostMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.JOBS)
    public ResponseEntity<?> createJob(@jakarta.validation.Valid @RequestBody JobRequest request) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            JobResponse response = jobService.createJob(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiSingleResponse.success(201, response));
        });
    }

    @GetMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.JOBS + com.procureiq.springboot_app.infra.config.ApiEndpoints.PATH_ID)
    public ResponseEntity<?> getJob(@PathVariable Long id) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            JobResponse response = jobService.getJob(id);
            return ResponseEntity.ok(ApiSingleResponse.success(200, response));
        });
    }

    @PutMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.JOBS + com.procureiq.springboot_app.infra.config.ApiEndpoints.PATH_ID)
    public ResponseEntity<?> updateJob(@PathVariable Long id, @jakarta.validation.Valid @RequestBody JobRequest request) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            JobResponse response = jobService.updateJob(id, request);
            return ResponseEntity.ok(ApiSingleResponse.success(200, response));
        });
    }

    @DeleteMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.JOBS + com.procureiq.springboot_app.infra.config.ApiEndpoints.PATH_ID)
    public ResponseEntity<?> deleteJob(@PathVariable Long id) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            jobService.deleteJob(id);
            return ResponseEntity.ok(ApiSingleResponse.success(200, "Deleted job successfully"));
        });
    }

    

    @GetMapping("/jobs/{id}/runs")
    public ResponseEntity<?> getJobRuns(@PathVariable Long id) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            List<JobRunResponse> response = jobService.getJobRuns(id);
            return ResponseEntity.ok(ApiListResponse.success(200, response));
        });
    }

    @PostMapping("/jobs/{id}/runs")
    public ResponseEntity<?> triggerJobRun(@PathVariable Long id) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            JobRunResponse response = jobService.triggerJobRun(id);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiSingleResponse.success(201, response));
        });
    }

    @GetMapping("/jobs/runs/{runId}")
    public ResponseEntity<?> getJobRun(@PathVariable Long runId) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            JobRunResponse response = jobService.getJobRun(runId);
            return ResponseEntity.ok(ApiSingleResponse.success(200, response));
        });
    }

    

    @GetMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.WORKFLOWS)
    public ResponseEntity<?> getAllWorkflows() {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            List<WorkflowResponse> response = jobService.getAllWorkflows();
            return ResponseEntity.ok(ApiListResponse.success(200, response));
        });
    }

    @PostMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.WORKFLOWS)
    public ResponseEntity<?> createWorkflow(@jakarta.validation.Valid @RequestBody WorkflowRequest request) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            WorkflowResponse response = jobService.createWorkflow(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiSingleResponse.success(201, response));
        });
    }

    @GetMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.WORKFLOWS + com.procureiq.springboot_app.infra.config.ApiEndpoints.PATH_ID)
    public ResponseEntity<?> getWorkflow(@PathVariable Long id) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            WorkflowResponse response = jobService.getWorkflow(id);
            return ResponseEntity.ok(ApiSingleResponse.success(200, response));
        });
    }

    @PutMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.WORKFLOWS + com.procureiq.springboot_app.infra.config.ApiEndpoints.PATH_ID)
    public ResponseEntity<?> updateWorkflow(@PathVariable Long id, @jakarta.validation.Valid @RequestBody WorkflowRequest request) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            WorkflowResponse response = jobService.updateWorkflow(id, request);
            return ResponseEntity.ok(ApiSingleResponse.success(200, response));
        });
    }

    @DeleteMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.WORKFLOWS + com.procureiq.springboot_app.infra.config.ApiEndpoints.PATH_ID)
    public ResponseEntity<?> deleteWorkflow(@PathVariable Long id) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            jobService.deleteWorkflow(id);
            return ResponseEntity.ok(ApiSingleResponse.success(200, "Deleted workflow successfully"));
        });
    }

    

    @GetMapping("/workflows/{id}/runs")
    public ResponseEntity<?> getWorkflowRuns(@PathVariable Long id) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            List<WorkflowRunResponse> response = jobService.getWorkflowRuns(id);
            return ResponseEntity.ok(ApiListResponse.success(200, response));
        });
    }

    @PostMapping("/workflows/{id}/runs")
    public ResponseEntity<?> triggerWorkflowRun(@PathVariable Long id) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            WorkflowRunResponse response = jobService.triggerWorkflowRun(id);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiSingleResponse.success(201, response));
        });
    }

    @GetMapping("/workflows/runs/{runId}")
    public ResponseEntity<?> getWorkflowRun(@PathVariable Long runId) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            WorkflowRunResponse response = jobService.getWorkflowRun(runId);
            return ResponseEntity.ok(ApiSingleResponse.success(200, response));
        });
    }
}
