package com.procureiq.springboot_app.features.fieldservice.service;

import com.procureiq.springboot_app.features.fieldservice.dto.request.ServiceResourceRequest;
import com.procureiq.springboot_app.features.fieldservice.dto.response.ServiceResourceResponse;
import com.procureiq.springboot_app.features.fieldservice.entity.ServiceResource;
import com.procureiq.springboot_app.features.fieldservice.repository.AppUserRepository;
import com.procureiq.springboot_app.features.fieldservice.repository.ServiceCrewRepository;
import com.procureiq.springboot_app.features.fieldservice.repository.ServiceResourceRepository;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServiceResourceService {

    private final ServiceResourceRepository serviceResourceRepository;
    private final AppUserRepository appUserRepository;
    private final ServiceCrewRepository serviceCrewRepository;
    private final Tracer tracer = GlobalOpenTelemetry.getTracer("springboot-app", "1.0.0");

    public ServiceResourceService(
            ServiceResourceRepository serviceResourceRepository,
            AppUserRepository appUserRepository,
            ServiceCrewRepository serviceCrewRepository) {
        this.serviceResourceRepository = serviceResourceRepository;
        this.appUserRepository = appUserRepository;
        this.serviceCrewRepository = serviceCrewRepository;
    }

    @Transactional(readOnly = true)
    public List<ServiceResourceResponse> getAllServiceResources() {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeServiceWithTracing(() -> {
            return serviceResourceRepository.findAll().stream()
                    .map(ServiceResourceResponse::fromEntity)
                    .collect(Collectors.toList());
        });
    }

    @Transactional
    public ServiceResourceResponse createServiceResource(ServiceResourceRequest request) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeServiceWithTracing(() -> {
            ServiceResource sr = new ServiceResource();
            sr.setName(request.name());
            if (request.userId() != null) {
                sr.setUser(appUserRepository.findById(request.userId()).orElse(null));
            }
            if (request.serviceCrewId() != null) {
                sr.setServiceCrew(serviceCrewRepository.findById(request.serviceCrewId()).orElse(null));
            }
            if (request.resourceType() != null) {
                sr.setResourceType(request.resourceType());
            }
            if (request.isActive() != null) {
                sr.setIsActive(request.isActive());
            }
            sr = serviceResourceRepository.save(sr);
            return ServiceResourceResponse.fromEntity(sr);
        });
    }

    @Transactional(readOnly = true)
    public ServiceResourceResponse getServiceResource(Long id) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeServiceWithTracing(() -> {
            ServiceResource sr = serviceResourceRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("ServiceResource not found: " + id));
            return ServiceResourceResponse.fromEntity(sr);
        });
    }

    @Transactional
    public ServiceResourceResponse updateServiceResource(Long id, ServiceResourceRequest request) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeServiceWithTracing(() -> {
            ServiceResource sr = serviceResourceRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("ServiceResource not found: " + id));
            sr.setName(request.name());
            if (request.userId() != null) {
                sr.setUser(appUserRepository.findById(request.userId()).orElse(null));
            } else {
                sr.setUser(null);
            }
            if (request.serviceCrewId() != null) {
                sr.setServiceCrew(serviceCrewRepository.findById(request.serviceCrewId()).orElse(null));
            } else {
                sr.setServiceCrew(null);
            }
            if (request.resourceType() != null) {
                sr.setResourceType(request.resourceType());
            }
            if (request.isActive() != null) {
                sr.setIsActive(request.isActive());
            }
            sr = serviceResourceRepository.save(sr);
            return ServiceResourceResponse.fromEntity(sr);
        });
    }

    @Transactional
    public void deleteServiceResource(Long id) {
        com.procureiq.springboot_app.infra.config.TracingHelper.executeServiceVoidWithTracing(() -> {
            serviceResourceRepository.deleteById(id);
        });
    }

    @Transactional(readOnly = true)
    public List<ServiceResourceResponse> getCandidatesForAppointment(Long appointmentId) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeServiceWithTracing(() -> {
            List<ServiceResource> candidates = serviceResourceRepository.findCandidatesForAppointment(appointmentId);
            if (candidates == null) {
                return java.util.Collections.emptyList();
            }
            return candidates.stream()
                    .map(ServiceResourceResponse::fromEntity)
                    .collect(Collectors.toList());
        });
    }
}
