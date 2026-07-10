package com.procureiq.springboot_app.features.fieldservice.service;

import com.procureiq.springboot_app.features.fieldservice.dto.ServiceTerritoryRequest;
import com.procureiq.springboot_app.features.fieldservice.dto.ServiceTerritoryResponse;
import com.procureiq.springboot_app.features.fieldservice.entity.ServiceTerritory;
import com.procureiq.springboot_app.features.fieldservice.repository.OperatingHoursRepository;
import com.procureiq.springboot_app.features.fieldservice.repository.ServiceTerritoryRepository;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServiceTerritoryService {

    private final ServiceTerritoryRepository serviceTerritoryRepository;
    private final OperatingHoursRepository operatingHoursRepository;
    private final Tracer tracer = GlobalOpenTelemetry.getTracer("springboot-app", "1.0.0");

    public ServiceTerritoryService(
            ServiceTerritoryRepository serviceTerritoryRepository,
            OperatingHoursRepository operatingHoursRepository) {
        this.serviceTerritoryRepository = serviceTerritoryRepository;
        this.operatingHoursRepository = operatingHoursRepository;
    }

    @Transactional(readOnly = true)
    public java.util.List<ServiceTerritoryResponse> getAllServiceTerritories() {
        Span span = tracer.spanBuilder("ServiceTerritoryService.getAllServiceTerritories").startSpan();
        try {
            return serviceTerritoryRepository.findAll().stream()
                    .map(st -> new ServiceTerritoryResponse(
                            st.getId(),
                            st.getName(),
                            st.getParentTerritory() != null ? st.getParentTerritory().getId() : null,
                            st.getOperatingHours() != null ? st.getOperatingHours().getId() : null,
                            st.getIsActive()
                    ))
                    .collect(java.util.stream.Collectors.toList());
        } finally {
            span.end();
        }
    }

    @Transactional
    public ServiceTerritoryResponse createServiceTerritory(ServiceTerritoryRequest request) {
        Span span = tracer.spanBuilder("ServiceTerritoryService.createServiceTerritory").startSpan();
        try {
            ServiceTerritory st = new ServiceTerritory();
            st.setName(request.name());
            if (request.operatingHoursId() != null) {
                st.setOperatingHours(operatingHoursRepository.findById(request.operatingHoursId()).orElse(null));
            }
            if (request.parentTerritoryId() != null) {
                st.setParentTerritory(serviceTerritoryRepository.findById(request.parentTerritoryId()).orElse(null));
            }
            if (request.isActive() != null) {
                st.setIsActive(request.isActive());
            }
            st = serviceTerritoryRepository.save(st);
            return new ServiceTerritoryResponse(
                    st.getId(),
                    st.getName(),
                    st.getParentTerritory() != null ? st.getParentTerritory().getId() : null,
                    st.getOperatingHours() != null ? st.getOperatingHours().getId() : null,
                    st.getIsActive()
            );
        } finally {
            span.end();
        }
    }

    @Transactional(readOnly = true)
    public ServiceTerritoryResponse getServiceTerritory(Long id) {
        Span span = tracer.spanBuilder("ServiceTerritoryService.getServiceTerritory").startSpan();
        try {
            ServiceTerritory st = serviceTerritoryRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("ServiceTerritory not found: " + id));
            return new ServiceTerritoryResponse(
                    st.getId(),
                    st.getName(),
                    st.getParentTerritory() != null ? st.getParentTerritory().getId() : null,
                    st.getOperatingHours() != null ? st.getOperatingHours().getId() : null,
                    st.getIsActive()
            );
        } finally {
            span.end();
        }
    }

    @Transactional
    public ServiceTerritoryResponse updateServiceTerritory(Long id, ServiceTerritoryRequest request) {
        Span span = tracer.spanBuilder("ServiceTerritoryService.updateServiceTerritory").startSpan();
        try {
            ServiceTerritory st = serviceTerritoryRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("ServiceTerritory not found: " + id));
            st.setName(request.name());
            if (request.operatingHoursId() != null) {
                st.setOperatingHours(operatingHoursRepository.findById(request.operatingHoursId()).orElse(null));
            } else {
                st.setOperatingHours(null);
            }
            if (request.parentTerritoryId() != null) {
                st.setParentTerritory(serviceTerritoryRepository.findById(request.parentTerritoryId()).orElse(null));
            } else {
                st.setParentTerritory(null);
            }
            if (request.isActive() != null) {
                st.setIsActive(request.isActive());
            }
            st = serviceTerritoryRepository.save(st);
            return new ServiceTerritoryResponse(
                    st.getId(),
                    st.getName(),
                    st.getParentTerritory() != null ? st.getParentTerritory().getId() : null,
                    st.getOperatingHours() != null ? st.getOperatingHours().getId() : null,
                    st.getIsActive()
            );
        } finally {
            span.end();
        }
    }

    @Transactional
    public void deleteServiceTerritory(Long id) {
        Span span = tracer.spanBuilder("ServiceTerritoryService.deleteServiceTerritory").startSpan();
        try {
            serviceTerritoryRepository.deleteById(id);
        } finally {
            span.end();
        }
    }
}
