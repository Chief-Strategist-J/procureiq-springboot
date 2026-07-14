package com.procureiq.springboot_app.features.fieldservice.service;

import com.procureiq.springboot_app.features.fieldservice.dto.request.OperatingHoursRequest;
import com.procureiq.springboot_app.features.fieldservice.dto.response.OperatingHoursResponse;
import com.procureiq.springboot_app.features.fieldservice.entity.OperatingHours;
import com.procureiq.springboot_app.features.fieldservice.repository.OperatingHoursRepository;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OperatingHoursService {

    private final OperatingHoursRepository operatingHoursRepository;
    private final Tracer tracer = GlobalOpenTelemetry.getTracer("springboot-app", "1.0.0");

    public OperatingHoursService(OperatingHoursRepository operatingHoursRepository) {
        this.operatingHoursRepository = operatingHoursRepository;
    }

    @Transactional(readOnly = true)
    public java.util.List<OperatingHoursResponse> getAllOperatingHours() {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeServiceWithTracing(() -> {
            return operatingHoursRepository.findAll().stream()
                    .map(oh -> new OperatingHoursResponse(oh.getId(), oh.getName(), oh.getTimezone()))
                    .collect(java.util.stream.Collectors.toList());
        });
    }

    @Transactional
    public OperatingHoursResponse createOperatingHours(OperatingHoursRequest request) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeServiceWithTracing(() -> {
            OperatingHours oh = new OperatingHours();
            oh.setName(request.name());
            oh.setTimezone(request.timezone() != null ? request.timezone() : "UTC");
            oh = operatingHoursRepository.save(oh);
            return new OperatingHoursResponse(oh.getId(), oh.getName(), oh.getTimezone());
        });
    }

    @Transactional(readOnly = true)
    public OperatingHoursResponse getOperatingHours(Long id) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeServiceWithTracing(() -> {
            OperatingHours oh = operatingHoursRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("OperatingHours not found: " + id));
            return new OperatingHoursResponse(oh.getId(), oh.getName(), oh.getTimezone());
        });
    }

    @Transactional
    public OperatingHoursResponse updateOperatingHours(Long id, OperatingHoursRequest request) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeServiceWithTracing(() -> {
            OperatingHours oh = operatingHoursRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("OperatingHours not found: " + id));
            oh.setName(request.name());
            oh.setTimezone(request.timezone());
            oh = operatingHoursRepository.save(oh);
            return new OperatingHoursResponse(oh.getId(), oh.getName(), oh.getTimezone());
        });
    }

    @Transactional
    public void deleteOperatingHours(Long id) {
        com.procureiq.springboot_app.infra.config.TracingHelper.executeServiceVoidWithTracing(() -> {
            operatingHoursRepository.deleteById(id);
        });
    }
}
