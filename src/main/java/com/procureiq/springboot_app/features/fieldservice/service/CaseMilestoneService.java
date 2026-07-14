package com.procureiq.springboot_app.features.fieldservice.service;

import com.procureiq.springboot_app.features.fieldservice.dto.request.CaseMilestoneRequest;
import com.procureiq.springboot_app.features.fieldservice.dto.response.CaseMilestoneResponse;
import com.procureiq.springboot_app.features.fieldservice.entity.Case;
import com.procureiq.springboot_app.features.fieldservice.entity.CaseMilestone;
import com.procureiq.springboot_app.features.fieldservice.entity.Milestone;
import com.procureiq.springboot_app.features.fieldservice.repository.CaseMilestoneRepository;
import com.procureiq.springboot_app.features.fieldservice.repository.CaseRepository;
import com.procureiq.springboot_app.features.fieldservice.repository.MilestoneRepository;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CaseMilestoneService {

    private final CaseMilestoneRepository caseMilestoneRepository;
    private final CaseRepository caseRepository;
    private final MilestoneRepository milestoneRepository;
    private final Tracer tracer = GlobalOpenTelemetry.getTracer("springboot-app", "1.0.0");

    public CaseMilestoneService(CaseMilestoneRepository caseMilestoneRepository,
                                CaseRepository caseRepository,
                                MilestoneRepository milestoneRepository) {
        this.caseMilestoneRepository = caseMilestoneRepository;
        this.caseRepository = caseRepository;
        this.milestoneRepository = milestoneRepository;
    }

    @Transactional
    public CaseMilestoneResponse createCaseMilestone(CaseMilestoneRequest request) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeServiceWithTracing(() -> {
            Case caseEntity = caseRepository.findById(request.caseId())
                    .orElseThrow(() -> new IllegalArgumentException("Case not found: " + request.caseId()));
            Milestone milestone = milestoneRepository.findById(request.milestoneId())
                    .orElseThrow(() -> new IllegalArgumentException("Milestone not found: " + request.milestoneId()));

            CaseMilestone cm = new CaseMilestone();
            cm.setCaseEntity(caseEntity);
            cm.setMilestone(milestone);
            cm.setStartedAt(request.startedAt() != null ? request.startedAt() : java.time.Instant.now());
            cm.setCompletedAt(request.completedAt());
            cm.setIsBreached(request.isBreached() != null ? request.isBreached() : false);
            cm = caseMilestoneRepository.save(cm);

            return mapToResponse(cm);
        });
    }

    @Transactional(readOnly = true)
    public CaseMilestoneResponse getCaseMilestone(Long id) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeServiceWithTracing(() -> {
            CaseMilestone cm = caseMilestoneRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("CaseMilestone not found: " + id));
            return mapToResponse(cm);
        });
    }

    @Transactional
    public CaseMilestoneResponse updateCaseMilestone(Long id, CaseMilestoneRequest request) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeServiceWithTracing(() -> {
            CaseMilestone cm = caseMilestoneRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("CaseMilestone not found: " + id));

            Case caseEntity = caseRepository.findById(request.caseId())
                    .orElseThrow(() -> new IllegalArgumentException("Case not found: " + request.caseId()));
            Milestone milestone = milestoneRepository.findById(request.milestoneId())
                    .orElseThrow(() -> new IllegalArgumentException("Milestone not found: " + request.milestoneId()));

            cm.setCaseEntity(caseEntity);
            cm.setMilestone(milestone);
            if (request.startedAt() != null) {
                cm.setStartedAt(request.startedAt());
            }
            if (request.completedAt() != null) {
                cm.setCompletedAt(request.completedAt());
            }
            if (request.isBreached() != null) {
                cm.setIsBreached(request.isBreached());
            }
            cm = caseMilestoneRepository.save(cm);

            return mapToResponse(cm);
        });
    }

    @Transactional
    public void deleteCaseMilestone(Long id) {
        com.procureiq.springboot_app.infra.config.TracingHelper.executeServiceVoidWithTracing(() -> {
            caseMilestoneRepository.deleteById(id);
        });
    }

    private CaseMilestoneResponse mapToResponse(CaseMilestone cm) {
        return new CaseMilestoneResponse(
            cm.getId(),
            cm.getCaseEntity().getId(),
            cm.getMilestone().getId(),
            cm.getStartedAt(),
            cm.getCompletedAt(),
            cm.getIsBreached()
        );
    }
}
