package com.procureiq.springboot_app.features.fieldservice.service;

import com.procureiq.springboot_app.features.fieldservice.dto.request.ResourceAbsenceRequest;
import com.procureiq.springboot_app.features.fieldservice.dto.response.ResourceAbsenceResponse;
import com.procureiq.springboot_app.features.fieldservice.entity.ResourceAbsence;
import com.procureiq.springboot_app.features.fieldservice.entity.ServiceResource;
import com.procureiq.springboot_app.features.fieldservice.repository.ResourceAbsenceRepository;
import com.procureiq.springboot_app.features.fieldservice.repository.ServiceResourceRepository;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ResourceAbsenceService {

    private final ResourceAbsenceRepository resourceAbsenceRepository;
    private final ServiceResourceRepository serviceResourceRepository;
    private final Tracer tracer = GlobalOpenTelemetry.getTracer("springboot-app", "1.0.0");

    public ResourceAbsenceService(ResourceAbsenceRepository resourceAbsenceRepository,
                                  ServiceResourceRepository serviceResourceRepository) {
        this.resourceAbsenceRepository = resourceAbsenceRepository;
        this.serviceResourceRepository = serviceResourceRepository;
    }

    @Transactional
    public ResourceAbsenceResponse createResourceAbsence(ResourceAbsenceRequest request) {
        Span span = tracer.spanBuilder("ResourceAbsenceService.createResourceAbsence").startSpan();
        try {
            ServiceResource sr = serviceResourceRepository.findById(request.serviceResourceId())
                    .orElseThrow(() -> new IllegalArgumentException("ServiceResource not found: " + request.serviceResourceId()));

            ResourceAbsence ra = new ResourceAbsence();
            ra.setServiceResource(sr);
            ra.setAbsenceType(request.absenceType());
            ra.setStartTime(request.startTime());
            ra.setEndTime(request.endTime());
            ra.setStatus(request.status() != null ? request.status() : "approved");
            ra = resourceAbsenceRepository.save(ra);

            return mapToResponse(ra);
        } finally {
            span.end();
        }
    }

    @Transactional(readOnly = true)
    public ResourceAbsenceResponse getResourceAbsence(Long id) {
        Span span = tracer.spanBuilder("ResourceAbsenceService.getResourceAbsence").startSpan();
        try {
            ResourceAbsence ra = resourceAbsenceRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("ResourceAbsence not found: " + id));
            return mapToResponse(ra);
        } finally {
            span.end();
        }
    }

    @Transactional
    public ResourceAbsenceResponse updateResourceAbsence(Long id, ResourceAbsenceRequest request) {
        Span span = tracer.spanBuilder("ResourceAbsenceService.updateResourceAbsence").startSpan();
        try {
            ResourceAbsence ra = resourceAbsenceRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("ResourceAbsence not found: " + id));

            ServiceResource sr = serviceResourceRepository.findById(request.serviceResourceId())
                    .orElseThrow(() -> new IllegalArgumentException("ServiceResource not found: " + request.serviceResourceId()));

            ra.setServiceResource(sr);
            ra.setAbsenceType(request.absenceType());
            ra.setStartTime(request.startTime());
            ra.setEndTime(request.endTime());
            ra.setStatus(request.status());
            ra = resourceAbsenceRepository.save(ra);

            return mapToResponse(ra);
        } finally {
            span.end();
        }
    }

    @Transactional
    public void deleteResourceAbsence(Long id) {
        Span span = tracer.spanBuilder("ResourceAbsenceService.deleteResourceAbsence").startSpan();
        try {
            resourceAbsenceRepository.deleteById(id);
        } finally {
            span.end();
        }
    }

    private ResourceAbsenceResponse mapToResponse(ResourceAbsence ra) {
        return new ResourceAbsenceResponse(
            ra.getId(),
            ra.getServiceResource().getId(),
            ra.getAbsenceType(),
            ra.getStartTime(),
            ra.getEndTime(),
            ra.getStatus()
        );
    }
}
