package com.procureiq.springboot_app.api.rest.v1.handlers;

import com.procureiq.springboot_app.features.jobs.dto.*;
import com.procureiq.springboot_app.features.jobs.service.JobService;
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
@RequestMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.API_V1)
public class JobController {

    private final JobService jobService;
    private final Tracer tracer = GlobalOpenTelemetry.getTracer("springboot-app", "1.0.0");

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    // --- Job Endpoints ---

    @GetMapping("/jobs")
    public ResponseEntity<?> getAllJobs() {
        Span span = tracer.spanBuilder("REST.getAllJobs").startSpan();
        try (Scope scope = span.makeCurrent()) {
            List<JobResponse> response = jobService.getAllJobs();
            span.setStatus(StatusCode.OK);
            return ResponseEntity.ok(ApiResponse.success(200, response));
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(500, e.getMessage()));
        } finally {
            span.end();
        }
    }

    @PostMapping("/jobs")
    public ResponseEntity<?> createJob(@RequestBody JobRequest request) {
        Span span = tracer.spanBuilder("REST.createJob").startSpan();
        try (Scope scope = span.makeCurrent()) {
            JobResponse response = jobService.createJob(request);
            span.setStatus(StatusCode.OK);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(201, response));
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(400, e.getMessage()));
        } finally {
            span.end();
        }
    }

    @GetMapping("/jobs/{id}")
    public ResponseEntity<?> getJob(@PathVariable Long id) {
        Span span = tracer.spanBuilder("REST.getJob").startSpan();
        try (Scope scope = span.makeCurrent()) {
            JobResponse response = jobService.getJob(id);
            span.setStatus(StatusCode.OK);
            return ResponseEntity.ok(ApiResponse.success(200, response));
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(400, e.getMessage()));
        } finally {
            span.end();
        }
    }

    @PutMapping("/jobs/{id}")
    public ResponseEntity<?> updateJob(@PathVariable Long id, @RequestBody JobRequest request) {
        Span span = tracer.spanBuilder("REST.updateJob").startSpan();
        try (Scope scope = span.makeCurrent()) {
            JobResponse response = jobService.updateJob(id, request);
            span.setStatus(StatusCode.OK);
            return ResponseEntity.ok(ApiResponse.success(200, response));
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(400, e.getMessage()));
        } finally {
            span.end();
        }
    }

    @DeleteMapping("/jobs/{id}")
    public ResponseEntity<?> deleteJob(@PathVariable Long id) {
        Span span = tracer.spanBuilder("REST.deleteJob").startSpan();
        try (Scope scope = span.makeCurrent()) {
            jobService.deleteJob(id);
            span.setStatus(StatusCode.OK);
            return ResponseEntity.ok(ApiResponse.success(200, "Deleted job successfully"));
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(400, e.getMessage()));
        } finally {
            span.end();
        }
    }

    // --- Job Run Endpoints ---

    @GetMapping("/jobs/{id}/runs")
    public ResponseEntity<?> getJobRuns(@PathVariable Long id) {
        Span span = tracer.spanBuilder("REST.getJobRuns").startSpan();
        try (Scope scope = span.makeCurrent()) {
            List<JobRunResponse> response = jobService.getJobRuns(id);
            span.setStatus(StatusCode.OK);
            return ResponseEntity.ok(ApiResponse.success(200, response));
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(500, e.getMessage()));
        } finally {
            span.end();
        }
    }

    @PostMapping("/jobs/{id}/runs")
    public ResponseEntity<?> triggerJobRun(@PathVariable Long id) {
        Span span = tracer.spanBuilder("REST.triggerJobRun").startSpan();
        try (Scope scope = span.makeCurrent()) {
            JobRunResponse response = jobService.triggerJobRun(id);
            span.setStatus(StatusCode.OK);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(201, response));
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(400, e.getMessage()));
        } finally {
            span.end();
        }
    }

    @GetMapping("/jobs/runs/{runId}")
    public ResponseEntity<?> getJobRun(@PathVariable Long runId) {
        Span span = tracer.spanBuilder("REST.getJobRun").startSpan();
        try (Scope scope = span.makeCurrent()) {
            JobRunResponse response = jobService.getJobRun(runId);
            span.setStatus(StatusCode.OK);
            return ResponseEntity.ok(ApiResponse.success(200, response));
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(400, e.getMessage()));
        } finally {
            span.end();
        }
    }

    // --- Workflow Endpoints ---

    @GetMapping("/workflows")
    public ResponseEntity<?> getAllWorkflows() {
        Span span = tracer.spanBuilder("REST.getAllWorkflows").startSpan();
        try (Scope scope = span.makeCurrent()) {
            List<WorkflowResponse> response = jobService.getAllWorkflows();
            span.setStatus(StatusCode.OK);
            return ResponseEntity.ok(ApiResponse.success(200, response));
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(500, e.getMessage()));
        } finally {
            span.end();
        }
    }

    @PostMapping("/workflows")
    public ResponseEntity<?> createWorkflow(@RequestBody WorkflowRequest request) {
        Span span = tracer.spanBuilder("REST.createWorkflow").startSpan();
        try (Scope scope = span.makeCurrent()) {
            WorkflowResponse response = jobService.createWorkflow(request);
            span.setStatus(StatusCode.OK);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(201, response));
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(400, e.getMessage()));
        } finally {
            span.end();
        }
    }

    @GetMapping("/workflows/{id}")
    public ResponseEntity<?> getWorkflow(@PathVariable Long id) {
        Span span = tracer.spanBuilder("REST.getWorkflow").startSpan();
        try (Scope scope = span.makeCurrent()) {
            WorkflowResponse response = jobService.getWorkflow(id);
            span.setStatus(StatusCode.OK);
            return ResponseEntity.ok(ApiResponse.success(200, response));
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(400, e.getMessage()));
        } finally {
            span.end();
        }
    }

    @PutMapping("/workflows/{id}")
    public ResponseEntity<?> updateWorkflow(@PathVariable Long id, @RequestBody WorkflowRequest request) {
        Span span = tracer.spanBuilder("REST.updateWorkflow").startSpan();
        try (Scope scope = span.makeCurrent()) {
            WorkflowResponse response = jobService.updateWorkflow(id, request);
            span.setStatus(StatusCode.OK);
            return ResponseEntity.ok(ApiResponse.success(200, response));
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(400, e.getMessage()));
        } finally {
            span.end();
        }
    }

    @DeleteMapping("/workflows/{id}")
    public ResponseEntity<?> deleteWorkflow(@PathVariable Long id) {
        Span span = tracer.spanBuilder("REST.deleteWorkflow").startSpan();
        try (Scope scope = span.makeCurrent()) {
            jobService.deleteWorkflow(id);
            span.setStatus(StatusCode.OK);
            return ResponseEntity.ok(ApiResponse.success(200, "Deleted workflow successfully"));
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(400, e.getMessage()));
        } finally {
            span.end();
        }
    }

    // --- Workflow Run Endpoints ---

    @GetMapping("/workflows/{id}/runs")
    public ResponseEntity<?> getWorkflowRuns(@PathVariable Long id) {
        Span span = tracer.spanBuilder("REST.getWorkflowRuns").startSpan();
        try (Scope scope = span.makeCurrent()) {
            List<WorkflowRunResponse> response = jobService.getWorkflowRuns(id);
            span.setStatus(StatusCode.OK);
            return ResponseEntity.ok(ApiResponse.success(200, response));
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(500, e.getMessage()));
        } finally {
            span.end();
        }
    }

    @PostMapping("/workflows/{id}/runs")
    public ResponseEntity<?> triggerWorkflowRun(@PathVariable Long id) {
        Span span = tracer.spanBuilder("REST.triggerWorkflowRun").startSpan();
        try (Scope scope = span.makeCurrent()) {
            WorkflowRunResponse response = jobService.triggerWorkflowRun(id);
            span.setStatus(StatusCode.OK);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(201, response));
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(400, e.getMessage()));
        } finally {
            span.end();
        }
    }

    @GetMapping("/workflows/runs/{runId}")
    public ResponseEntity<?> getWorkflowRun(@PathVariable Long runId) {
        Span span = tracer.spanBuilder("REST.getWorkflowRun").startSpan();
        try (Scope scope = span.makeCurrent()) {
            WorkflowRunResponse response = jobService.getWorkflowRun(runId);
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
