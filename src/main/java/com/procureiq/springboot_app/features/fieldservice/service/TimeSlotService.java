package com.procureiq.springboot_app.features.fieldservice.service;

import com.procureiq.springboot_app.features.fieldservice.dto.TimeSlotRequest;
import com.procureiq.springboot_app.features.fieldservice.dto.TimeSlotResponse;
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
        Span span = tracer.spanBuilder("TimeSlotService.createTimeSlot").startSpan();
        try {
            OperatingHours oh = operatingHoursRepository.findById(request.operatingHoursId())
                    .orElseThrow(() -> new IllegalArgumentException("OperatingHours not found: " + request.operatingHoursId()));

            TimeSlot ts = new TimeSlot();
            ts.setOperatingHours(oh);
            ts.setDayOfWeek(request.dayOfWeek());
            ts.setStartTime(request.startTime());
            ts.setEndTime(request.endTime());
            ts = timeSlotRepository.save(ts);

            return mapToResponse(ts);
        } finally {
            span.end();
        }
    }

    @Transactional(readOnly = true)
    public TimeSlotResponse getTimeSlot(Long id) {
        Span span = tracer.spanBuilder("TimeSlotService.getTimeSlot").startSpan();
        try {
            TimeSlot ts = timeSlotRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("TimeSlot not found: " + id));
            return mapToResponse(ts);
        } finally {
            span.end();
        }
    }

    @Transactional
    public TimeSlotResponse updateTimeSlot(Long id, TimeSlotRequest request) {
        Span span = tracer.spanBuilder("TimeSlotService.updateTimeSlot").startSpan();
        try {
            TimeSlot ts = timeSlotRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("TimeSlot not found: " + id));

            OperatingHours oh = operatingHoursRepository.findById(request.operatingHoursId())
                    .orElseThrow(() -> new IllegalArgumentException("OperatingHours not found: " + request.operatingHoursId()));

            ts.setOperatingHours(oh);
            ts.setDayOfWeek(request.dayOfWeek());
            ts.setStartTime(request.startTime());
            ts.setEndTime(request.endTime());
            ts = timeSlotRepository.save(ts);

            return mapToResponse(ts);
        } finally {
            span.end();
        }
    }

    @Transactional
    public void deleteTimeSlot(Long id) {
        Span span = tracer.spanBuilder("TimeSlotService.deleteTimeSlot").startSpan();
        try {
            timeSlotRepository.deleteById(id);
        } finally {
            span.end();
        }
    }

    private TimeSlotResponse mapToResponse(TimeSlot ts) {
        return new TimeSlotResponse(
            ts.getId(),
            ts.getOperatingHours().getId(),
            ts.getDayOfWeek(),
            ts.getStartTime(),
            ts.getEndTime()
        );
    }
}
