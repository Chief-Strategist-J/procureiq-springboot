package com.procureiq.springboot_app.features.fieldservice.service;

import com.procureiq.springboot_app.features.fieldservice.dto.request.ServiceCrewRequest;
import com.procureiq.springboot_app.features.fieldservice.dto.response.ServiceCrewResponse;
import com.procureiq.springboot_app.features.fieldservice.entity.ServiceCrew;
import com.procureiq.springboot_app.features.fieldservice.repository.ServiceCrewRepository;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServiceCrewService {

    private final ServiceCrewRepository serviceCrewRepository;
    private final Tracer tracer = GlobalOpenTelemetry.getTracer("springboot-app", "1.0.0");

    public ServiceCrewService(ServiceCrewRepository serviceCrewRepository) {
        this.serviceCrewRepository = serviceCrewRepository;
    }

    @Transactional
    public ServiceCrewResponse createServiceCrew(ServiceCrewRequest request) {
        Span span = tracer.spanBuilder("ServiceCrewService.createServiceCrew").startSpan();
        try {
            ServiceCrew sc = new ServiceCrew();
            sc.setName(request.name());
            sc = serviceCrewRepository.save(sc);
            return mapToResponse(sc);
        } finally {
            span.end();
        }
    }

    @Transactional(readOnly = true)
    public ServiceCrewResponse getServiceCrew(Long id) {
        Span span = tracer.spanBuilder("ServiceCrewService.getServiceCrew").startSpan();
        try {
            ServiceCrew sc = serviceCrewRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("ServiceCrew not found: " + id));
            return mapToResponse(sc);
        } finally {
            span.end();
        }
    }

    @Transactional
    public ServiceCrewResponse updateServiceCrew(Long id, ServiceCrewRequest request) {
        Span span = tracer.spanBuilder("ServiceCrewService.updateServiceCrew").startSpan();
        try {
            ServiceCrew sc = serviceCrewRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("ServiceCrew not found: " + id));
            sc.setName(request.name());
            sc = serviceCrewRepository.save(sc);
            return mapToResponse(sc);
        } finally {
            span.end();
        }
    }

    @Transactional
    public void deleteServiceCrew(Long id) {
        Span span = tracer.spanBuilder("ServiceCrewService.deleteServiceCrew").startSpan();
        try {
            serviceCrewRepository.deleteById(id);
        } finally {
            span.end();
        }
    }

    private ServiceCrewResponse mapToResponse(ServiceCrew sc) {
        return new ServiceCrewResponse(
            sc.getId(),
            sc.getName()
        );
    }
}
