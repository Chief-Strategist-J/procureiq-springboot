package com.procureiq.springboot_app.features.fieldservice.service;

import com.procureiq.springboot_app.features.fieldservice.dto.request.SkillRequest;
import com.procureiq.springboot_app.features.fieldservice.dto.response.SkillResponse;
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
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeServiceWithTracing(() -> {
            Skill skill = new Skill();
            skill.setName(request.name());
            skill.setDescription(request.description());
            skill = skillRepository.save(skill);
            return mapToResponse(skill);
        });
    }

    @Transactional(readOnly = true)
    public SkillResponse getSkill(Long id) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeServiceWithTracing(() -> {
            Skill skill = skillRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Skill not found: " + id));
            return mapToResponse(skill);
        });
    }

    @Transactional
    public SkillResponse updateSkill(Long id, SkillRequest request) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeServiceWithTracing(() -> {
            Skill skill = skillRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Skill not found: " + id));
            if (request.name() != null) {
                skill.setName(request.name());
            }
            if (request.description() != null) {
                skill.setDescription(request.description());
            }
            skill = skillRepository.save(skill);
            return mapToResponse(skill);
        });
    }

    @Transactional
    public void deleteSkill(Long id) {
        com.procureiq.springboot_app.infra.config.TracingHelper.executeServiceVoidWithTracing(() -> {
            skillRepository.deleteById(id);
        });
    }

    private SkillResponse mapToResponse(Skill skill) {
        return new SkillResponse(
            skill.getId(),
            skill.getName(),
            skill.getDescription()
        );
    }
}
