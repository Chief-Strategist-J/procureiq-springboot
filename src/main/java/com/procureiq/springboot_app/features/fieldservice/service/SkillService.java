package com.procureiq.springboot_app.features.fieldservice.service;

import com.procureiq.springboot_app.features.fieldservice.dto.SkillRequest;
import com.procureiq.springboot_app.features.fieldservice.dto.SkillResponse;
import com.procureiq.springboot_app.features.fieldservice.entity.Skill;
import com.procureiq.springboot_app.features.fieldservice.repository.SkillRepository;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SkillService {

    private final SkillRepository skillRepository;
    private final Tracer tracer = GlobalOpenTelemetry.getTracer("springboot-app", "1.0.0");

    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    @Transactional
    public SkillResponse createSkill(SkillRequest request) {
        Span span = tracer.spanBuilder("SkillService.createSkill").startSpan();
        try {
            Skill skill = new Skill();
            skill.setName(request.name());
            skill.setDescription(request.description());
            skill = skillRepository.save(skill);
            return mapToResponse(skill);
        } finally {
            span.end();
        }
    }

    @Transactional(readOnly = true)
    public SkillResponse getSkill(Long id) {
        Span span = tracer.spanBuilder("SkillService.getSkill").startSpan();
        try {
            Skill skill = skillRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Skill not found: " + id));
            return mapToResponse(skill);
        } finally {
            span.end();
        }
    }

    @Transactional
    public SkillResponse updateSkill(Long id, SkillRequest request) {
        Span span = tracer.spanBuilder("SkillService.updateSkill").startSpan();
        try {
            Skill skill = skillRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Skill not found: " + id));
            skill.setName(request.name());
            skill.setDescription(request.description());
            skill = skillRepository.save(skill);
            return mapToResponse(skill);
        } finally {
            span.end();
        }
    }

    @Transactional
    public void deleteSkill(Long id) {
        Span span = tracer.spanBuilder("SkillService.deleteSkill").startSpan();
        try {
            skillRepository.deleteById(id);
        } finally {
            span.end();
        }
    }

    private SkillResponse mapToResponse(Skill skill) {
        return new SkillResponse(
            skill.getId(),
            skill.getName(),
            skill.getDescription()
        );
    }
}
