package com.procureiq.springboot_app.features.fieldservice.service;

import com.procureiq.springboot_app.features.fieldservice.dto.request.MilestoneRequest;
import com.procureiq.springboot_app.features.fieldservice.dto.response.MilestoneResponse;
import com.procureiq.springboot_app.features.fieldservice.entity.EntitlementProcess;
import com.procureiq.springboot_app.features.fieldservice.entity.Milestone;
import com.procureiq.springboot_app.features.fieldservice.repository.EntitlementProcessRepository;
import com.procureiq.springboot_app.features.fieldservice.repository.MilestoneRepository;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MilestoneService {

    private final MilestoneRepository milestoneRepository;
    private final EntitlementProcessRepository entitlementProcessRepository;
    private final Tracer tracer = GlobalOpenTelemetry.getTracer("springboot-app", "1.0.0");

    public MilestoneService(MilestoneRepository milestoneRepository,
                            EntitlementProcessRepository entitlementProcessRepository) {
        this.milestoneRepository = milestoneRepository;
        this.entitlementProcessRepository = entitlementProcessRepository;
    }

    @Transactional
    public MilestoneResponse createMilestone(MilestoneRequest request) {
        Span span = tracer.spanBuilder("MilestoneService.createMilestone").startSpan();
        try {
            EntitlementProcess ep = entitlementProcessRepository.findById(request.entitlementProcessId())
                    .orElseThrow(() -> new IllegalArgumentException("EntitlementProcess not found: " + request.entitlementProcessId()));

            Milestone m = new Milestone();
            m.setEntitlementProcess(ep);
            m.setName(request.name());
            m.setTargetMinutes(request.targetMinutes());
            m.setSequence(request.sequence());
            m = milestoneRepository.save(m);

            return mapToResponse(m);
        } finally {
            span.end();
        }
    }

    @Transactional(readOnly = true)
    public MilestoneResponse getMilestone(Long id) {
        Span span = tracer.spanBuilder("MilestoneService.getMilestone").startSpan();
        try {
            Milestone m = milestoneRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Milestone not found: " + id));
            return mapToResponse(m);
        } finally {
            span.end();
        }
    }

    @Transactional
    public MilestoneResponse updateMilestone(Long id, MilestoneRequest request) {
        Span span = tracer.spanBuilder("MilestoneService.updateMilestone").startSpan();
        try {
            Milestone m = milestoneRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Milestone not found: " + id));

            EntitlementProcess ep = entitlementProcessRepository.findById(request.entitlementProcessId())
                    .orElseThrow(() -> new IllegalArgumentException("EntitlementProcess not found: " + request.entitlementProcessId()));

            m.setEntitlementProcess(ep);
            m.setName(request.name());
            m.setTargetMinutes(request.targetMinutes());
            m.setSequence(request.sequence());
            m = milestoneRepository.save(m);

            return mapToResponse(m);
        } finally {
            span.end();
        }
    }

    @Transactional
    public void deleteMilestone(Long id) {
        Span span = tracer.spanBuilder("MilestoneService.deleteMilestone").startSpan();
        try {
            milestoneRepository.deleteById(id);
        } finally {
            span.end();
        }
    }

    private MilestoneResponse mapToResponse(Milestone m) {
        return new MilestoneResponse(
            m.getId(),
            m.getEntitlementProcess().getId(),
            m.getName(),
            m.getTargetMinutes(),
            m.getSequence()
        );
    }
}
