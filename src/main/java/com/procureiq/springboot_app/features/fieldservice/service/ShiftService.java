package com.procureiq.springboot_app.features.fieldservice.service;

import com.procureiq.springboot_app.features.fieldservice.dto.request.ShiftRequest;
import com.procureiq.springboot_app.features.fieldservice.dto.response.ShiftResponse;
import com.procureiq.springboot_app.features.fieldservice.entity.ServiceResource;
import com.procureiq.springboot_app.features.fieldservice.entity.ServiceTerritory;
import com.procureiq.springboot_app.features.fieldservice.entity.Shift;
import com.procureiq.springboot_app.features.fieldservice.repository.ServiceResourceRepository;
import com.procureiq.springboot_app.features.fieldservice.repository.ServiceTerritoryRepository;
import com.procureiq.springboot_app.features.fieldservice.repository.ShiftRepository;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ShiftService {

    private final ShiftRepository shiftRepository;
    private final ServiceResourceRepository serviceResourceRepository;
    private final ServiceTerritoryRepository serviceTerritoryRepository;
    private final Tracer tracer = GlobalOpenTelemetry.getTracer("springboot-app", "1.0.0");

    public ShiftService(ShiftRepository shiftRepository,
                        ServiceResourceRepository serviceResourceRepository,
                        ServiceTerritoryRepository serviceTerritoryRepository) {
        this.shiftRepository = shiftRepository;
        this.serviceResourceRepository = serviceResourceRepository;
        this.serviceTerritoryRepository = serviceTerritoryRepository;
    }

    @Transactional
    public ShiftResponse createShift(ShiftRequest request) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeServiceWithTracing(() -> {
            ServiceResource sr = serviceResourceRepository.findById(request.serviceResourceId())
                    .orElseThrow(() -> new IllegalArgumentException("ServiceResource not found: " + request.serviceResourceId()));
            ServiceTerritory st = serviceTerritoryRepository.findById(request.serviceTerritoryId())
                    .orElseThrow(() -> new IllegalArgumentException("ServiceTerritory not found: " + request.serviceTerritoryId()));

            Shift shift = new Shift();
            shift.setServiceResource(sr);
            shift.setServiceTerritory(st);
            shift.setStartTime(request.startTime());
            shift.setEndTime(request.endTime());
            shift.setShiftType(request.shiftType() != null ? request.shiftType() : "standard");
            shift = shiftRepository.save(shift);

            return mapToResponse(shift);
        });
    }

    @Transactional(readOnly = true)
    public ShiftResponse getShift(Long id) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeServiceWithTracing(() -> {
            Shift shift = shiftRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Shift not found: " + id));
            return mapToResponse(shift);
        });
    }

    @Transactional
    public ShiftResponse updateShift(Long id, ShiftRequest request) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeServiceWithTracing(() -> {
            Shift shift = shiftRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Shift not found: " + id));

            ServiceResource sr = serviceResourceRepository.findById(request.serviceResourceId())
                    .orElseThrow(() -> new IllegalArgumentException("ServiceResource not found: " + request.serviceResourceId()));
            ServiceTerritory st = serviceTerritoryRepository.findById(request.serviceTerritoryId())
                    .orElseThrow(() -> new IllegalArgumentException("ServiceTerritory not found: " + request.serviceTerritoryId()));

            shift.setServiceResource(sr);
            shift.setServiceTerritory(st);
            shift.setStartTime(request.startTime());
            shift.setEndTime(request.endTime());
            shift.setShiftType(request.shiftType());
            shift = shiftRepository.save(shift);

            return mapToResponse(shift);
        });
    }

    @Transactional
    public void deleteShift(Long id) {
        com.procureiq.springboot_app.infra.config.TracingHelper.executeServiceVoidWithTracing(() -> {
            shiftRepository.deleteById(id);
        });
    }

    private ShiftResponse mapToResponse(Shift shift) {
        return new ShiftResponse(
            shift.getId(),
            shift.getServiceResource().getId(),
            shift.getServiceTerritory().getId(),
            shift.getStartTime(),
            shift.getEndTime(),
            shift.getShiftType()
        );
    }
}
