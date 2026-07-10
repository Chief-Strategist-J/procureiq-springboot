package com.procureiq.springboot_app.features.fieldservice.service;

import com.procureiq.springboot_app.features.fieldservice.dto.CaseMilestoneRequest;
import com.procureiq.springboot_app.features.fieldservice.dto.CaseMilestoneResponse;
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
        Span span = tracer.spanBuilder("CaseMilestoneService.createCaseMilestone").startSpan();
        try {
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
        } finally {
            span.end();
        }
    }

    @Transactional(readOnly = true)
    public CaseMilestoneResponse getCaseMilestone(Long id) {
        Span span = tracer.spanBuilder("CaseMilestoneService.getCaseMilestone").startSpan();
        try {
            CaseMilestone cm = caseMilestoneRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("CaseMilestone not found: " + id));
            return mapToResponse(cm);
        } finally {
            span.end();
        }
    }

    @Transactional
    public CaseMilestoneResponse updateCaseMilestone(Long id, CaseMilestoneRequest request) {
        Span span = tracer.spanBuilder("CaseMilestoneService.updateCaseMilestone").startSpan();
        try {
            CaseMilestone cm = caseMilestoneRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("CaseMilestone not found: " + id));

            Case caseEntity = caseRepository.findById(request.caseId())
                    .orElseThrow(() -> new IllegalArgumentException("Case not found: " + request.caseId()));
            Milestone milestone = milestoneRepository.findById(request.milestoneId())
                    .orElseThrow(() -> new IllegalArgumentException("Milestone not found: " + request.milestoneId()));

            cm.setCaseEntity(caseEntity);
            cm.setMilestone(milestone);
            cm.setStartedAt(request.startedAt());
            cm.setCompletedAt(request.completedAt());
            cm.setIsBreached(request.isBreached());
            cm = caseMilestoneRepository.save(cm);

            return mapToResponse(cm);
        } finally {
            span.end();
        }
    }

    @Transactional
    public void deleteCaseMilestone(Long id) {
        Span span = tracer.spanBuilder("CaseMilestoneService.deleteCaseMilestone").startSpan();
        try {
            caseMilestoneRepository.deleteById(id);
        } finally {
            span.end();
        }
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
