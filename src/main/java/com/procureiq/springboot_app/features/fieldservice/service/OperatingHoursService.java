package com.procureiq.springboot_app.features.fieldservice.service;

import com.procureiq.springboot_app.features.fieldservice.dto.OperatingHoursRequest;
import com.procureiq.springboot_app.features.fieldservice.dto.OperatingHoursResponse;
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
        Span span = tracer.spanBuilder("OperatingHoursService.getAllOperatingHours").startSpan();
        try {
            return operatingHoursRepository.findAll().stream()
                    .map(oh -> new OperatingHoursResponse(oh.getId(), oh.getName(), oh.getTimezone()))
                    .collect(java.util.stream.Collectors.toList());
        } finally {
            span.end();
        }
    }

    @Transactional
    public OperatingHoursResponse createOperatingHours(OperatingHoursRequest request) {
        Span span = tracer.spanBuilder("OperatingHoursService.createOperatingHours").startSpan();
        try {
            OperatingHours oh = new OperatingHours();
            oh.setName(request.name());
            oh.setTimezone(request.timezone() != null ? request.timezone() : "UTC");
            oh = operatingHoursRepository.save(oh);
            return new OperatingHoursResponse(oh.getId(), oh.getName(), oh.getTimezone());
        } finally {
            span.end();
        }
    }

    @Transactional(readOnly = true)
    public OperatingHoursResponse getOperatingHours(Long id) {
        Span span = tracer.spanBuilder("OperatingHoursService.getOperatingHours").startSpan();
        try {
            OperatingHours oh = operatingHoursRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("OperatingHours not found: " + id));
            return new OperatingHoursResponse(oh.getId(), oh.getName(), oh.getTimezone());
        } finally {
            span.end();
        }
    }

    @Transactional
    public OperatingHoursResponse updateOperatingHours(Long id, OperatingHoursRequest request) {
        Span span = tracer.spanBuilder("OperatingHoursService.updateOperatingHours").startSpan();
        try {
            OperatingHours oh = operatingHoursRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("OperatingHours not found: " + id));
            oh.setName(request.name());
            oh.setTimezone(request.timezone());
            oh = operatingHoursRepository.save(oh);
            return new OperatingHoursResponse(oh.getId(), oh.getName(), oh.getTimezone());
        } finally {
            span.end();
        }
    }

    @Transactional
    public void deleteOperatingHours(Long id) {
        Span span = tracer.spanBuilder("OperatingHoursService.deleteOperatingHours").startSpan();
        try {
            operatingHoursRepository.deleteById(id);
        } finally {
            span.end();
        }
    }
}
