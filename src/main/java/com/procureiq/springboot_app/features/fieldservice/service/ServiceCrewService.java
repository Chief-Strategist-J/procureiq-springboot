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
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeServiceWithTracing(() -> {
            ServiceCrew sc = new ServiceCrew();
            sc.setName(request.name());
            sc = serviceCrewRepository.save(sc);
            return mapToResponse(sc);
        });
    }

    @Transactional(readOnly = true)
    public ServiceCrewResponse getServiceCrew(Long id) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeServiceWithTracing(() -> {
            ServiceCrew sc = serviceCrewRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("ServiceCrew not found: " + id));
            return mapToResponse(sc);
        });
    }

    @Transactional
    public ServiceCrewResponse updateServiceCrew(Long id, ServiceCrewRequest request) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeServiceWithTracing(() -> {
            ServiceCrew sc = serviceCrewRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("ServiceCrew not found: " + id));
            if (request.name() != null) {
                sc.setName(request.name());
            }
            sc = serviceCrewRepository.save(sc);
            return mapToResponse(sc);
        });
    }

    @Transactional
    public void deleteServiceCrew(Long id) {
        com.procureiq.springboot_app.infra.config.TracingHelper.executeServiceVoidWithTracing(() -> {
            serviceCrewRepository.deleteById(id);
        });
    }

    private ServiceCrewResponse mapToResponse(ServiceCrew sc) {
        return new ServiceCrewResponse(
            sc.getId(),
            sc.getName()
        );
    }
}
