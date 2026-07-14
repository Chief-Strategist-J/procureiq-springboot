package com.procureiq.springboot_app.features.jobs.service;

import com.procureiq.springboot_app.features.campaigns.entity.Organization;
import com.procureiq.springboot_app.features.campaigns.repository.OrganizationRepository;
import com.procureiq.springboot_app.features.jobs.dto.request.*;
import com.procureiq.springboot_app.features.jobs.dto.response.*;
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

    

    @Transactional(readOnly = true)
    public List<JobResponse> getAllJobs() {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeServiceWithTracing(() -> {
            return jobRepository.findAll().stream()
                    .map(JobResponse::fromEntity)
                    .collect(Collectors.toList());
        });
    }

    @Transactional
    public JobResponse createJob(JobRequest request) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeServiceWithTracing(() -> {
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
            return JobResponse.fromEntity(j);
        });
    }

    @Transactional(readOnly = true)
    public JobResponse getJob(Long id) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeServiceWithTracing(() -> {
            Job j = jobRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Job not found: " + id));
            return JobResponse.fromEntity(j);
        });
    }

    @Transactional
    public JobResponse updateJob(Long id, JobRequest request) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeServiceWithTracing(() -> {
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
            return JobResponse.fromEntity(j);
        });
    }

    @Transactional
    public void deleteJob(Long id) {
        com.procureiq.springboot_app.infra.config.TracingHelper.executeServiceVoidWithTracing(() -> {
            Job j = jobRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Job not found: " + id));
            jobRepository.delete(j);
        });
    }

    

    @Transactional
    public JobRunResponse triggerJobRun(Long jobId) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeServiceWithTracing(() -> {
            Job j = jobRepository.findById(jobId)
                    .orElseThrow(() -> new IllegalArgumentException("Job not found: " + jobId));
            JobRun run = new JobRun();
            run.setJobId(j.getId());
            run.setStatus("running");
            run.setStartedAt(Instant.now());
            run = jobRunRepository.save(run);
            return JobRunResponse.fromEntity(run);
        });
    }

    @Transactional(readOnly = true)
    public List<JobRunResponse> getJobRuns(Long jobId) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeServiceWithTracing(() -> {
            return jobRunRepository.findByJobId(jobId).stream()
                    .map(JobRunResponse::fromEntity)
                    .collect(Collectors.toList());
        });
    }

    @Transactional(readOnly = true)
    public JobRunResponse getJobRun(Long runId) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeServiceWithTracing(() -> {
            JobRun run = jobRunRepository.findByRawId(runId)
                    .orElseThrow(() -> new IllegalArgumentException("Job run not found: " + runId));
            return JobRunResponse.fromEntity(run);
        });
    }

    

    @Transactional(readOnly = true)
    public List<WorkflowResponse> getAllWorkflows() {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeServiceWithTracing(() -> {
            return workflowRepository.findAll().stream()
                    .map(WorkflowResponse::fromEntity)
                    .collect(Collectors.toList());
        });
    }

    @Transactional
    public WorkflowResponse createWorkflow(WorkflowRequest request) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeServiceWithTracing(() -> {
            Organization org = getOrCreateOrganization(request.orgId());
            Workflow w = new Workflow();
            w.setOrganization(org);
            w.setName(request.name());
            w.setStatus(request.status() != null ? request.status() : "draft");
            w = workflowRepository.save(w);
            return WorkflowResponse.fromEntity(w);
        });
    }

    @Transactional(readOnly = true)
    public WorkflowResponse getWorkflow(Long id) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeServiceWithTracing(() -> {
            Workflow w = workflowRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Workflow not found: " + id));
            return WorkflowResponse.fromEntity(w);
        });
    }

    @Transactional
    public WorkflowResponse updateWorkflow(Long id, WorkflowRequest request) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeServiceWithTracing(() -> {
            Workflow w = workflowRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Workflow not found: " + id));
            w.setName(request.name());
            if (request.status() != null) {
                w.setStatus(request.status());
            }
            w = workflowRepository.save(w);
            return WorkflowResponse.fromEntity(w);
        });
    }

    @Transactional
    public void deleteWorkflow(Long id) {
        com.procureiq.springboot_app.infra.config.TracingHelper.executeServiceVoidWithTracing(() -> {
            Workflow w = workflowRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Workflow not found: " + id));
            workflowRepository.delete(w);
        });
    }

    

    @Transactional
    public WorkflowRunResponse triggerWorkflowRun(Long workflowId) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeServiceWithTracing(() -> {
            Workflow w = workflowRepository.findById(workflowId)
                    .orElseThrow(() -> new IllegalArgumentException("Workflow not found: " + workflowId));
            WorkflowRun run = new WorkflowRun();
            run.setWorkflowId(w.getId());
            run.setStatus("running");
            run.setStartedAt(Instant.now());
            run = workflowRunRepository.save(run);
            return WorkflowRunResponse.fromEntity(run);
        });
    }

    @Transactional(readOnly = true)
    public List<WorkflowRunResponse> getWorkflowRuns(Long workflowId) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeServiceWithTracing(() -> {
            return workflowRunRepository.findByWorkflowId(workflowId).stream()
                    .map(WorkflowRunResponse::fromEntity)
                    .collect(Collectors.toList());
        });
    }

    @Transactional(readOnly = true)
    public WorkflowRunResponse getWorkflowRun(Long runId) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeServiceWithTracing(() -> {
            WorkflowRun run = workflowRunRepository.findByRawId(runId)
                    .orElseThrow(() -> new IllegalArgumentException("Workflow run not found: " + runId));
            return WorkflowRunResponse.fromEntity(run);
        });
    }
}
