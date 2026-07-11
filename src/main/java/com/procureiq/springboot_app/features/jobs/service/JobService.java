package com.procureiq.springboot_app.features.jobs.service;

import com.procureiq.springboot_app.features.campaigns.entity.Organization;
import com.procureiq.springboot_app.features.campaigns.repository.OrganizationRepository;
import com.procureiq.springboot_app.features.jobs.dto.*;
import com.procureiq.springboot_app.features.jobs.entity.*;
import com.procureiq.springboot_app.features.jobs.repository.*;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobService {

    private final JobRepository jobRepository;
    private final JobRunRepository jobRunRepository;
    private final WorkflowRepository workflowRepository;
    private final WorkflowRunRepository workflowRunRepository;
    private final OrganizationRepository organizationRepository;
    private final Tracer tracer = GlobalOpenTelemetry.getTracer("springboot-app", "1.0.0");

    public JobService(
            JobRepository jobRepository,
            JobRunRepository jobRunRepository,
            WorkflowRepository workflowRepository,
            WorkflowRunRepository workflowRunRepository,
            OrganizationRepository organizationRepository) {
        this.jobRepository = jobRepository;
        this.jobRunRepository = jobRunRepository;
        this.workflowRepository = workflowRepository;
        this.workflowRunRepository = workflowRunRepository;
        this.organizationRepository = organizationRepository;
    }

    private Organization getOrCreateOrganization(Long orgId) {
        return organizationRepository.findById(orgId).orElseGet(() -> {
            Organization org = new Organization();
            org.setId(orgId);
            org.setName("Org " + orgId);
            return organizationRepository.save(org);
        });
    }

    // --- Job CRUD ---

    @Transactional(readOnly = true)
    public List<JobResponse> getAllJobs() {
        Span span = tracer.spanBuilder("JobService.getAllJobs").startSpan();
        try {
            return jobRepository.findAll().stream()
                    .map(j -> new JobResponse(
                            j.getId(),
                            j.getOrganization().getId(),
                            j.getCategoryId(),
                            j.getName(),
                            j.getStatus(),
                            j.getConfig(),
                            j.getCreatedAt(),
                            j.getUpdatedAt()))
                    .collect(Collectors.toList());
        } finally {
            span.end();
        }
    }

    @Transactional
    public JobResponse createJob(JobRequest request) {
        Span span = tracer.spanBuilder("JobService.createJob").startSpan();
        try {
            Organization org = getOrCreateOrganization(request.orgId());
            Job j = new Job();
            j.setOrganization(org);
            j.setCategoryId(request.categoryId());
            j.setName(request.name());
            j.setStatus(request.status() != null ? request.status() : "active");
            if (request.config() != null) {
                j.setConfig(request.config());
            }
            j = jobRepository.save(j);
            return new JobResponse(
                    j.getId(),
                    j.getOrganization().getId(),
                    j.getCategoryId(),
                    j.getName(),
                    j.getStatus(),
                    j.getConfig(),
                    j.getCreatedAt(),
                    j.getUpdatedAt());
        } finally {
            span.end();
        }
    }

    @Transactional(readOnly = true)
    public JobResponse getJob(Long id) {
        Span span = tracer.spanBuilder("JobService.getJob").startSpan();
        try {
            Job j = jobRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Job not found: " + id));
            return new JobResponse(
                    j.getId(),
                    j.getOrganization().getId(),
                    j.getCategoryId(),
                    j.getName(),
                    j.getStatus(),
                    j.getConfig(),
                    j.getCreatedAt(),
                    j.getUpdatedAt());
        } finally {
            span.end();
        }
    }

    @Transactional
    public JobResponse updateJob(Long id, JobRequest request) {
        Span span = tracer.spanBuilder("JobService.updateJob").startSpan();
        try {
            Job j = jobRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Job not found: " + id));
            j.setName(request.name());
            if (request.status() != null) {
                j.setStatus(request.status());
            }
            if (request.config() != null) {
                j.setConfig(request.config());
            }
            j.setCategoryId(request.categoryId());
            j = jobRepository.save(j);
            return new JobResponse(
                    j.getId(),
                    j.getOrganization().getId(),
                    j.getCategoryId(),
                    j.getName(),
                    j.getStatus(),
                    j.getConfig(),
                    j.getCreatedAt(),
                    j.getUpdatedAt());
        } finally {
            span.end();
        }
    }

    @Transactional
    public void deleteJob(Long id) {
        Span span = tracer.spanBuilder("JobService.deleteJob").startSpan();
        try {
            Job j = jobRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Job not found: " + id));
            jobRepository.delete(j);
        } finally {
            span.end();
        }
    }

    // --- Job Runs ---

    @Transactional
    public JobRunResponse triggerJobRun(Long jobId) {
        Span span = tracer.spanBuilder("JobService.triggerJobRun").startSpan();
        try {
            Job j = jobRepository.findById(jobId)
                    .orElseThrow(() -> new IllegalArgumentException("Job not found: " + jobId));
            JobRun run = new JobRun();
            run.setJobId(j.getId());
            run.setStatus("running");
            run.setStartedAt(Instant.now());
            run = jobRunRepository.save(run);
            return new JobRunResponse(
                    run.getId(),
                    run.getJobId(),
                    run.getStatus(),
                    run.getStartedAt(),
                    run.getCompletedAt(),
                    run.getCreatedAt());
        } finally {
            span.end();
        }
    }

    @Transactional(readOnly = true)
    public List<JobRunResponse> getJobRuns(Long jobId) {
        Span span = tracer.spanBuilder("JobService.getJobRuns").startSpan();
        try {
            return jobRunRepository.findByJobId(jobId).stream()
                    .map(run -> new JobRunResponse(
                            run.getId(),
                            run.getJobId(),
                            run.getStatus(),
                            run.getStartedAt(),
                            run.getCompletedAt(),
                            run.getCreatedAt()))
                    .collect(Collectors.toList());
        } finally {
            span.end();
        }
    }

    @Transactional(readOnly = true)
    public JobRunResponse getJobRun(Long runId) {
        Span span = tracer.spanBuilder("JobService.getJobRun").startSpan();
        try {
            JobRun run = jobRunRepository.findByRawId(runId)
                    .orElseThrow(() -> new IllegalArgumentException("Job run not found: " + runId));
            return new JobRunResponse(
                    run.getId(),
                    run.getJobId(),
                    run.getStatus(),
                    run.getStartedAt(),
                    run.getCompletedAt(),
                    run.getCreatedAt());
        } finally {
            span.end();
        }
    }

    // --- Workflow CRUD ---

    @Transactional(readOnly = true)
    public List<WorkflowResponse> getAllWorkflows() {
        Span span = tracer.spanBuilder("JobService.getAllWorkflows").startSpan();
        try {
            return workflowRepository.findAll().stream()
                    .map(w -> new WorkflowResponse(
                            w.getId(),
                            w.getOrganization().getId(),
                            w.getName(),
                            w.getStatus(),
                            w.getCreatedAt(),
                            w.getUpdatedAt()))
                    .collect(Collectors.toList());
        } finally {
            span.end();
        }
    }

    @Transactional
    public WorkflowResponse createWorkflow(WorkflowRequest request) {
        Span span = tracer.spanBuilder("JobService.createWorkflow").startSpan();
        try {
            Organization org = getOrCreateOrganization(request.orgId());
            Workflow w = new Workflow();
            w.setOrganization(org);
            w.setName(request.name());
            w.setStatus(request.status() != null ? request.status() : "draft");
            w = workflowRepository.save(w);
            return new WorkflowResponse(
                    w.getId(),
                    w.getOrganization().getId(),
                    w.getName(),
                    w.getStatus(),
                    w.getCreatedAt(),
                    w.getUpdatedAt());
        } finally {
            span.end();
        }
    }

    @Transactional(readOnly = true)
    public WorkflowResponse getWorkflow(Long id) {
        Span span = tracer.spanBuilder("JobService.getWorkflow").startSpan();
        try {
            Workflow w = workflowRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Workflow not found: " + id));
            return new WorkflowResponse(
                    w.getId(),
                    w.getOrganization().getId(),
                    w.getName(),
                    w.getStatus(),
                    w.getCreatedAt(),
                    w.getUpdatedAt());
        } finally {
            span.end();
        }
    }

    @Transactional
    public WorkflowResponse updateWorkflow(Long id, WorkflowRequest request) {
        Span span = tracer.spanBuilder("JobService.updateWorkflow").startSpan();
        try {
            Workflow w = workflowRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Workflow not found: " + id));
            w.setName(request.name());
            if (request.status() != null) {
                w.setStatus(request.status());
            }
            w = workflowRepository.save(w);
            return new WorkflowResponse(
                    w.getId(),
                    w.getOrganization().getId(),
                    w.getName(),
                    w.getStatus(),
                    w.getCreatedAt(),
                    w.getUpdatedAt());
        } finally {
            span.end();
        }
    }

    @Transactional
    public void deleteWorkflow(Long id) {
        Span span = tracer.spanBuilder("JobService.deleteWorkflow").startSpan();
        try {
            Workflow w = workflowRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Workflow not found: " + id));
            workflowRepository.delete(w);
        } finally {
            span.end();
        }
    }

    // --- Workflow Runs ---

    @Transactional
    public WorkflowRunResponse triggerWorkflowRun(Long workflowId) {
        Span span = tracer.spanBuilder("JobService.triggerWorkflowRun").startSpan();
        try {
            Workflow w = workflowRepository.findById(workflowId)
                    .orElseThrow(() -> new IllegalArgumentException("Workflow not found: " + workflowId));
            WorkflowRun run = new WorkflowRun();
            run.setWorkflowId(w.getId());
            run.setStatus("running");
            run.setStartedAt(Instant.now());
            run = workflowRunRepository.save(run);
            return new WorkflowRunResponse(
                    run.getId(),
                    run.getWorkflowId(),
                    run.getStatus(),
                    run.getStartedAt(),
                    run.getCompletedAt(),
                    run.getCreatedAt());
        } finally {
            span.end();
        }
    }

    @Transactional(readOnly = true)
    public List<WorkflowRunResponse> getWorkflowRuns(Long workflowId) {
        Span span = tracer.spanBuilder("JobService.getWorkflowRuns").startSpan();
        try {
            return workflowRunRepository.findByWorkflowId(workflowId).stream()
                    .map(run -> new WorkflowRunResponse(
                            run.getId(),
                            run.getWorkflowId(),
                            run.getStatus(),
                            run.getStartedAt(),
                            run.getCompletedAt(),
                            run.getCreatedAt()))
                    .collect(Collectors.toList());
        } finally {
            span.end();
        }
    }

    @Transactional(readOnly = true)
    public WorkflowRunResponse getWorkflowRun(Long runId) {
        Span span = tracer.spanBuilder("JobService.getWorkflowRun").startSpan();
        try {
            WorkflowRun run = workflowRunRepository.findByRawId(runId)
                    .orElseThrow(() -> new IllegalArgumentException("Workflow run not found: " + runId));
            return new WorkflowRunResponse(
                    run.getId(),
                    run.getWorkflowId(),
                    run.getStatus(),
                    run.getStartedAt(),
                    run.getCompletedAt(),
                    run.getCreatedAt());
        } finally {
            span.end();
        }
    }
}
