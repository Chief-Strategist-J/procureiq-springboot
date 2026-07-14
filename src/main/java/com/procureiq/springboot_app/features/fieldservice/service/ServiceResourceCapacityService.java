package com.procureiq.springboot_app.features.fieldservice.service;

import com.procureiq.springboot_app.features.fieldservice.dto.request.ServiceResourceCapacityRequest;
import com.procureiq.springboot_app.features.fieldservice.dto.response.ServiceResourceCapacityResponse;
import com.procureiq.springboot_app.features.fieldservice.entity.ServiceResource;
import com.procureiq.springboot_app.features.fieldservice.entity.ServiceResourceCapacity;
import com.procureiq.springboot_app.features.fieldservice.repository.ServiceResourceCapacityRepository;
import com.procureiq.springboot_app.features.fieldservice.repository.ServiceResourceRepository;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServiceResourceCapacityService {

    private final ServiceResourceCapacityRepository serviceResourceCapacityRepository;
    private final ServiceResourceRepository serviceResourceRepository;
    private final Tracer tracer = GlobalOpenTelemetry.getTracer("springboot-app", "1.0.0");

    public ServiceResourceCapacityService(ServiceResourceCapacityRepository serviceResourceCapacityRepository,
                                         ServiceResourceRepository serviceResourceRepository) {
        this.serviceResourceCapacityRepository = serviceResourceCapacityRepository;
        this.serviceResourceRepository = serviceResourceRepository;
    }

    @Transactional
    public ServiceResourceCapacityResponse createServiceResourceCapacity(ServiceResourceCapacityRequest request) {
        Span span = tracer.spanBuilder("ServiceResourceCapacityService.createServiceResourceCapacity").startSpan();
        try {
            ServiceResource sr = serviceResourceRepository.findById(request.serviceResourceId())
                    .orElseThrow(() -> new IllegalArgumentException("ServiceResource not found: " + request.serviceResourceId()));

            ServiceResourceCapacity src = new ServiceResourceCapacity();
            src.setServiceResource(sr);
            src.setCapacityType(request.capacityType());
            src.setCapacityValue(request.capacityValue());
            src.setStartDate(request.startDate());
            src.setEndDate(request.endDate());
            src = serviceResourceCapacityRepository.save(src);

            return mapToResponse(src);
        } finally {
            span.end();
        }
    }

    @Transactional(readOnly = true)
    public ServiceResourceCapacityResponse getServiceResourceCapacity(Long id) {
        Span span = tracer.spanBuilder("ServiceResourceCapacityService.getServiceResourceCapacity").startSpan();
        try {
            ServiceResourceCapacity src = serviceResourceCapacityRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("ServiceResourceCapacity not found: " + id));
            return mapToResponse(src);
        } finally {
            span.end();
        }
    }

    @Transactional
    public ServiceResourceCapacityResponse updateServiceResourceCapacity(Long id, ServiceResourceCapacityRequest request) {
        Span span = tracer.spanBuilder("ServiceResourceCapacityService.updateServiceResourceCapacity").startSpan();
        try {
            ServiceResourceCapacity src = serviceResourceCapacityRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("ServiceResourceCapacity not found: " + id));

            ServiceResource sr = serviceResourceRepository.findById(request.serviceResourceId())
                    .orElseThrow(() -> new IllegalArgumentException("ServiceResource not found: " + request.serviceResourceId()));

            src.setServiceResource(sr);
            src.setCapacityType(request.capacityType());
            src.setCapacityValue(request.capacityValue());
            src.setStartDate(request.startDate());
            src.setEndDate(request.endDate());
            src = serviceResourceCapacityRepository.save(src);

            return mapToResponse(src);
        } finally {
            span.end();
        }
    }

    @Transactional
    public void deleteServiceResourceCapacity(Long id) {
        Span span = tracer.spanBuilder("ServiceResourceCapacityService.deleteServiceResourceCapacity").startSpan();
        try {
            serviceResourceCapacityRepository.deleteById(id);
        } finally {
            span.end();
        }
    }

    private ServiceResourceCapacityResponse mapToResponse(ServiceResourceCapacity src) {
        return new ServiceResourceCapacityResponse(
            src.getId(),
            src.getServiceResource().getId(),
            src.getCapacityType(),
            src.getCapacityValue(),
            src.getStartDate(),
            src.getEndDate()
        );
    }
}
