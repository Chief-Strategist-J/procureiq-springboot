package com.procureiq.springboot_app.features.fieldservice.service;

import com.procureiq.springboot_app.features.fieldservice.dto.request.TimeSlotRequest;
import com.procureiq.springboot_app.features.fieldservice.dto.response.TimeSlotResponse;
import com.procureiq.springboot_app.features.fieldservice.entity.OperatingHours;
import com.procureiq.springboot_app.features.fieldservice.entity.TimeSlot;
import com.procureiq.springboot_app.features.fieldservice.repository.OperatingHoursRepository;
import com.procureiq.springboot_app.features.fieldservice.repository.TimeSlotRepository;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TimeSlotService {

    private final TimeSlotRepository timeSlotRepository;
    private final OperatingHoursRepository operatingHoursRepository;
    private final Tracer tracer = GlobalOpenTelemetry.getTracer("springboot-app", "1.0.0");

    public TimeSlotService(TimeSlotRepository timeSlotRepository, OperatingHoursRepository operatingHoursRepository) {
        this.timeSlotRepository = timeSlotRepository;
        this.operatingHoursRepository = operatingHoursRepository;
    }

    @Transactional
    public TimeSlotResponse createTimeSlot(TimeSlotRequest request) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeServiceWithTracing(() -> {
            OperatingHours oh = operatingHoursRepository.findById(request.operatingHoursId())
                    .orElseThrow(() -> new IllegalArgumentException("OperatingHours not found: " + request.operatingHoursId()));

            TimeSlot ts = new TimeSlot();
            ts.setOperatingHours(oh);
            ts.setDayOfWeek(request.dayOfWeek() != null ? request.dayOfWeek().shortValue() : null);
            ts.setStartTime(request.startTime());
            ts.setEndTime(request.endTime());
            ts = timeSlotRepository.save(ts);

            return mapToResponse(ts);
        });
    }

    @Transactional(readOnly = true)
    public TimeSlotResponse getTimeSlot(Long id) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeServiceWithTracing(() -> {
            TimeSlot ts = timeSlotRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("TimeSlot not found: " + id));
            return mapToResponse(ts);
        });
    }

    @Transactional
    public TimeSlotResponse updateTimeSlot(Long id, TimeSlotRequest request) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeServiceWithTracing(() -> {
            TimeSlot ts = timeSlotRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("TimeSlot not found: " + id));

            OperatingHours oh = operatingHoursRepository.findById(request.operatingHoursId())
                    .orElseThrow(() -> new IllegalArgumentException("OperatingHours not found: " + request.operatingHoursId()));

            ts.setOperatingHours(oh);
            if (request.dayOfWeek() != null) {
                ts.setDayOfWeek(request.dayOfWeek().shortValue());
            }
            if (request.startTime() != null) {
                ts.setStartTime(request.startTime());
            }
            if (request.endTime() != null) {
                ts.setEndTime(request.endTime());
            }
            ts = timeSlotRepository.save(ts);

            return mapToResponse(ts);
        });
    }

    @Transactional
    public void deleteTimeSlot(Long id) {
        com.procureiq.springboot_app.infra.config.TracingHelper.executeServiceVoidWithTracing(() -> {
            timeSlotRepository.deleteById(id);
        });
    }

    private TimeSlotResponse mapToResponse(TimeSlot ts) {
        return TimeSlotResponse.fromEntity(ts);
    }
}
