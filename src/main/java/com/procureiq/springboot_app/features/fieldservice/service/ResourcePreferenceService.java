package com.procureiq.springboot_app.features.fieldservice.service;

import com.procureiq.springboot_app.features.fieldservice.dto.request.ResourcePreferenceRequest;
import com.procureiq.springboot_app.features.fieldservice.dto.response.ResourcePreferenceResponse;
import com.procureiq.springboot_app.features.fieldservice.entity.ResourcePreference;
import com.procureiq.springboot_app.features.fieldservice.entity.ServiceResource;
import com.procureiq.springboot_app.features.fieldservice.repository.ResourcePreferenceRepository;
import com.procureiq.springboot_app.features.fieldservice.repository.ServiceResourceRepository;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ResourcePreferenceService {

    private final ResourcePreferenceRepository resourcePreferenceRepository;
    private final ServiceResourceRepository serviceResourceRepository;
    private final Tracer tracer = GlobalOpenTelemetry.getTracer("springboot-app", "1.0.0");

    public ResourcePreferenceService(ResourcePreferenceRepository resourcePreferenceRepository,
                                     ServiceResourceRepository serviceResourceRepository) {
        this.resourcePreferenceRepository = resourcePreferenceRepository;
        this.serviceResourceRepository = serviceResourceRepository;
    }

    @Transactional
    public ResourcePreferenceResponse createResourcePreference(ResourcePreferenceRequest request) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeServiceWithTracing(() -> {
            ServiceResource sr = serviceResourceRepository.findById(request.serviceResourceId())
                    .orElseThrow(() -> new IllegalArgumentException("ServiceResource not found: " + request.serviceResourceId()));

            ResourcePreference rp = new ResourcePreference();
            rp.setServiceResource(sr);
            rp.setRelatedRecordType(request.relatedRecordType());
            rp.setRelatedRecordId(request.relatedRecordId());
            rp.setPreferenceType(request.preferenceType());
            rp = resourcePreferenceRepository.save(rp);

            return mapToResponse(rp);
        });
    }

    @Transactional(readOnly = true)
    public ResourcePreferenceResponse getResourcePreference(Long id) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeServiceWithTracing(() -> {
            ResourcePreference rp = resourcePreferenceRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("ResourcePreference not found: " + id));
            return mapToResponse(rp);
        });
    }

    @Transactional
    public ResourcePreferenceResponse updateResourcePreference(Long id, ResourcePreferenceRequest request) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeServiceWithTracing(() -> {
            ResourcePreference rp = resourcePreferenceRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("ResourcePreference not found: " + id));

            ServiceResource sr = serviceResourceRepository.findById(request.serviceResourceId())
                    .orElseThrow(() -> new IllegalArgumentException("ServiceResource not found: " + request.serviceResourceId()));

            rp.setServiceResource(sr);
            if (request.relatedRecordType() != null) {
                rp.setRelatedRecordType(request.relatedRecordType());
            }
            if (request.relatedRecordId() != null) {
                rp.setRelatedRecordId(request.relatedRecordId());
            }
            if (request.preferenceType() != null) {
                rp.setPreferenceType(request.preferenceType());
            }
            rp = resourcePreferenceRepository.save(rp);

            return mapToResponse(rp);
        });
    }

    @Transactional
    public void deleteResourcePreference(Long id) {
        com.procureiq.springboot_app.infra.config.TracingHelper.executeServiceVoidWithTracing(() -> {
            resourcePreferenceRepository.deleteById(id);
        });
    }

    private ResourcePreferenceResponse mapToResponse(ResourcePreference rp) {
        return new ResourcePreferenceResponse(
            rp.getId(),
            rp.getServiceResource().getId(),
            rp.getRelatedRecordType(),
            rp.getRelatedRecordId(),
            rp.getPreferenceType()
        );
    }
}
