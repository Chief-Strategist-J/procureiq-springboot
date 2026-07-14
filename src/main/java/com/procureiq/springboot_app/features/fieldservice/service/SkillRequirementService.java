package com.procureiq.springboot_app.features.fieldservice.service;

import com.procureiq.springboot_app.features.fieldservice.dto.request.SkillRequirementRequest;
import com.procureiq.springboot_app.features.fieldservice.dto.response.SkillRequirementResponse;
import com.procureiq.springboot_app.features.fieldservice.entity.Skill;
import com.procureiq.springboot_app.features.fieldservice.entity.SkillRequirement;
import com.procureiq.springboot_app.features.fieldservice.repository.SkillRepository;
import com.procureiq.springboot_app.features.fieldservice.repository.SkillRequirementRepository;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SkillRequirementService {

    private final SkillRequirementRepository skillRequirementRepository;
    private final SkillRepository skillRepository;
    private final Tracer tracer = GlobalOpenTelemetry.getTracer("springboot-app", "1.0.0");

    public SkillRequirementService(SkillRequirementRepository skillRequirementRepository, SkillRepository skillRepository) {
        this.skillRequirementRepository = skillRequirementRepository;
        this.skillRepository = skillRepository;
    }

    @Transactional
    public SkillRequirementResponse createSkillRequirement(SkillRequirementRequest request) {
        Span span = tracer.spanBuilder("SkillRequirementService.createSkillRequirement").startSpan();
        try {
            Skill skill = skillRepository.findById(request.skillId())
                    .orElseThrow(() -> new IllegalArgumentException("Skill not found: " + request.skillId()));

            SkillRequirement sr = new SkillRequirement();
            sr.setSkill(skill);
            sr.setRequiredForType(request.requiredForType());
            sr.setRequiredForId(request.requiredForId());
            sr.setMinSkillLevel(request.minSkillLevel() != null ? request.minSkillLevel() : 1);
            sr = skillRequirementRepository.save(sr);

            return mapToResponse(sr);
        } finally {
            span.end();
        }
    }

    @Transactional(readOnly = true)
    public SkillRequirementResponse getSkillRequirement(Long id) {
        Span span = tracer.spanBuilder("SkillRequirementService.getSkillRequirement").startSpan();
        try {
            SkillRequirement sr = skillRequirementRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("SkillRequirement not found: " + id));
            return mapToResponse(sr);
        } finally {
            span.end();
        }
    }

    @Transactional
    public SkillRequirementResponse updateSkillRequirement(Long id, SkillRequirementRequest request) {
        Span span = tracer.spanBuilder("SkillRequirementService.updateSkillRequirement").startSpan();
        try {
            SkillRequirement sr = skillRequirementRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("SkillRequirement not found: " + id));

            Skill skill = skillRepository.findById(request.skillId())
                    .orElseThrow(() -> new IllegalArgumentException("Skill not found: " + request.skillId()));

            sr.setSkill(skill);
            sr.setRequiredForType(request.requiredForType());
            sr.setRequiredForId(request.requiredForId());
            sr.setMinSkillLevel(request.minSkillLevel());
            sr = skillRequirementRepository.save(sr);

            return mapToResponse(sr);
        } finally {
            span.end();
        }
    }

    @Transactional
    public void deleteSkillRequirement(Long id) {
        Span span = tracer.spanBuilder("SkillRequirementService.deleteSkillRequirement").startSpan();
        try {
            skillRequirementRepository.deleteById(id);
        } finally {
            span.end();
        }
    }

    private SkillRequirementResponse mapToResponse(SkillRequirement sr) {
        return new SkillRequirementResponse(
            sr.getId(),
            sr.getSkill().getId(),
            sr.getRequiredForType(),
            sr.getRequiredForId(),
            sr.getMinSkillLevel()
        );
    }
}
