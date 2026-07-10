package com.procureiq.springboot_app.features.fieldservice.service;

import com.procureiq.springboot_app.features.fieldservice.dto.ServiceResourceRequest;
import com.procureiq.springboot_app.features.fieldservice.dto.ServiceResourceResponse;
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

    @Transactional
    public ServiceResourceResponse createServiceResource(ServiceResourceRequest request) {
        Span span = tracer.spanBuilder("ServiceResourceService.createServiceResource").startSpan();
        try {
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
            return new ServiceResourceResponse(
                    sr.getId(),
                    sr.getName(),
                    sr.getUser() != null ? sr.getUser().getId() : null,
                    sr.getServiceCrew() != null ? sr.getServiceCrew().getId() : null,
                    sr.getResourceType(),
                    sr.getIsActive()
            );
        } finally {
            span.end();
        }
    }

    @Transactional(readOnly = true)
    public ServiceResourceResponse getServiceResource(Long id) {
        Span span = tracer.spanBuilder("ServiceResourceService.getServiceResource").startSpan();
        try {
            ServiceResource sr = serviceResourceRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("ServiceResource not found: " + id));
            return new ServiceResourceResponse(
                    sr.getId(),
                    sr.getName(),
                    sr.getUser() != null ? sr.getUser().getId() : null,
                    sr.getServiceCrew() != null ? sr.getServiceCrew().getId() : null,
                    sr.getResourceType(),
                    sr.getIsActive()
            );
        } finally {
            span.end();
        }
    }

    @Transactional
    public ServiceResourceResponse updateServiceResource(Long id, ServiceResourceRequest request) {
        Span span = tracer.spanBuilder("ServiceResourceService.updateServiceResource").startSpan();
        try {
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
            return new ServiceResourceResponse(
                    sr.getId(),
                    sr.getName(),
                    sr.getUser() != null ? sr.getUser().getId() : null,
                    sr.getServiceCrew() != null ? sr.getServiceCrew().getId() : null,
                    sr.getResourceType(),
                    sr.getIsActive()
            );
        } finally {
            span.end();
        }
    }

    @Transactional
    public void deleteServiceResource(Long id) {
        Span span = tracer.spanBuilder("ServiceResourceService.deleteServiceResource").startSpan();
        try {
            serviceResourceRepository.deleteById(id);
        } finally {
            span.end();
        }
    }

    @Transactional(readOnly = true)
    public List<ServiceResourceResponse> getCandidatesForAppointment(Long appointmentId) {
        Span span = tracer.spanBuilder("ServiceResourceService.getCandidatesForAppointment").startSpan();
        try {
            List<ServiceResource> candidates = serviceResourceRepository.findCandidatesForAppointment(appointmentId);
            if (candidates == null) {
                return java.util.Collections.emptyList();
            }
            return candidates.stream()
                    .map(sr -> new ServiceResourceResponse(
                            sr.getId(),
                            sr.getName(),
                            sr.getUser() != null ? sr.getUser().getId() : null,
                            sr.getServiceCrew() != null ? sr.getServiceCrew().getId() : null,
                            sr.getResourceType(),
                            sr.getIsActive()
                    ))
                    .collect(Collectors.toList());
        } finally {
            span.end();
        }
    }
}
