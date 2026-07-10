package com.procureiq.springboot_app.features.fieldservice.service;

import com.procureiq.springboot_app.features.fieldservice.dto.ServiceResourceSkillRequest;
import com.procureiq.springboot_app.features.fieldservice.dto.ServiceResourceSkillResponse;
import com.procureiq.springboot_app.features.fieldservice.entity.ServiceResource;
import com.procureiq.springboot_app.features.fieldservice.entity.ServiceResourceSkill;
import com.procureiq.springboot_app.features.fieldservice.entity.Skill;
import com.procureiq.springboot_app.features.fieldservice.repository.ServiceResourceRepository;
import com.procureiq.springboot_app.features.fieldservice.repository.ServiceResourceSkillRepository;
import com.procureiq.springboot_app.features.fieldservice.repository.SkillRepository;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServiceResourceSkillService {

    private final ServiceResourceSkillRepository serviceResourceSkillRepository;
    private final ServiceResourceRepository serviceResourceRepository;
    private final SkillRepository skillRepository;
    private final Tracer tracer = GlobalOpenTelemetry.getTracer("springboot-app", "1.0.0");

    public ServiceResourceSkillService(ServiceResourceSkillRepository serviceResourceSkillRepository,
                                       ServiceResourceRepository serviceResourceRepository,
                                       SkillRepository skillRepository) {
        this.serviceResourceSkillRepository = serviceResourceSkillRepository;
        this.serviceResourceRepository = serviceResourceRepository;
        this.skillRepository = skillRepository;
    }

    @Transactional
    public ServiceResourceSkillResponse createServiceResourceSkill(ServiceResourceSkillRequest request) {
        Span span = tracer.spanBuilder("ServiceResourceSkillService.createServiceResourceSkill").startSpan();
        try {
            ServiceResource sr = serviceResourceRepository.findById(request.serviceResourceId())
                    .orElseThrow(() -> new IllegalArgumentException("ServiceResource not found: " + request.serviceResourceId()));
            Skill skill = skillRepository.findById(request.skillId())
                    .orElseThrow(() -> new IllegalArgumentException("Skill not found: " + request.skillId()));

            ServiceResourceSkill srs = new ServiceResourceSkill();
            srs.setServiceResource(sr);
            srs.setSkill(skill);
            srs.setSkillLevel(request.skillLevel() != null ? request.skillLevel() : 1);
            srs.setValidFrom(request.validFrom() != null ? request.validFrom() : java.time.LocalDate.now());
            srs.setValidTo(request.validTo());
            srs = serviceResourceSkillRepository.save(srs);

            return mapToResponse(srs);
        } finally {
            span.end();
        }
    }

    @Transactional(readOnly = true)
    public ServiceResourceSkillResponse getServiceResourceSkill(Long id) {
        Span span = tracer.spanBuilder("ServiceResourceSkillService.getServiceResourceSkill").startSpan();
        try {
            ServiceResourceSkill srs = serviceResourceSkillRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("ServiceResourceSkill not found: " + id));
            return mapToResponse(srs);
        } finally {
            span.end();
        }
    }

    @Transactional
    public ServiceResourceSkillResponse updateServiceResourceSkill(Long id, ServiceResourceSkillRequest request) {
        Span span = tracer.spanBuilder("ServiceResourceSkillService.updateServiceResourceSkill").startSpan();
        try {
            ServiceResourceSkill srs = serviceResourceSkillRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("ServiceResourceSkill not found: " + id));

            ServiceResource sr = serviceResourceRepository.findById(request.serviceResourceId())
                    .orElseThrow(() -> new IllegalArgumentException("ServiceResource not found: " + request.serviceResourceId()));
            Skill skill = skillRepository.findById(request.skillId())
                    .orElseThrow(() -> new IllegalArgumentException("Skill not found: " + request.skillId()));

            srs.setServiceResource(sr);
            srs.setSkill(skill);
            srs.setSkillLevel(request.skillLevel());
            srs.setValidFrom(request.validFrom());
            srs.setValidTo(request.validTo());
            srs = serviceResourceSkillRepository.save(srs);

            return mapToResponse(srs);
        } finally {
            span.end();
        }
    }

    @Transactional
    public void deleteServiceResourceSkill(Long id) {
        Span span = tracer.spanBuilder("ServiceResourceSkillService.deleteServiceResourceSkill").startSpan();
        try {
            serviceResourceSkillRepository.deleteById(id);
        } finally {
            span.end();
        }
    }

    private ServiceResourceSkillResponse mapToResponse(ServiceResourceSkill srs) {
        return new ServiceResourceSkillResponse(
            srs.getId(),
            srs.getServiceResource().getId(),
            srs.getSkill().getId(),
            srs.getSkillLevel(),
            srs.getValidFrom(),
            srs.getValidTo()
        );
    }
}
