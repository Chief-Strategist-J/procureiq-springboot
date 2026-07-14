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
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeServiceWithTracing(() -> {
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
        });
    }

    @Transactional(readOnly = true)
    public ServiceResourceCapacityResponse getServiceResourceCapacity(Long id) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeServiceWithTracing(() -> {
            ServiceResourceCapacity src = serviceResourceCapacityRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("ServiceResourceCapacity not found: " + id));
            return mapToResponse(src);
        });
    }

    @Transactional
    public ServiceResourceCapacityResponse updateServiceResourceCapacity(Long id, ServiceResourceCapacityRequest request) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeServiceWithTracing(() -> {
            ServiceResourceCapacity src = serviceResourceCapacityRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("ServiceResourceCapacity not found: " + id));

            ServiceResource sr = serviceResourceRepository.findById(request.serviceResourceId())
                    .orElseThrow(() -> new IllegalArgumentException("ServiceResource not found: " + request.serviceResourceId()));

            src.setServiceResource(sr);
            if (request.capacityType() != null) {
                src.setCapacityType(request.capacityType());
            }
            if (request.capacityValue() != null) {
                src.setCapacityValue(request.capacityValue());
            }
            if (request.startDate() != null) {
                src.setStartDate(request.startDate());
            }
            if (request.endDate() != null) {
                src.setEndDate(request.endDate());
            }
            src = serviceResourceCapacityRepository.save(src);

            return mapToResponse(src);
        });
    }

    @Transactional
    public void deleteServiceResourceCapacity(Long id) {
        com.procureiq.springboot_app.infra.config.TracingHelper.executeServiceVoidWithTracing(() -> {
            serviceResourceCapacityRepository.deleteById(id);
        });
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
