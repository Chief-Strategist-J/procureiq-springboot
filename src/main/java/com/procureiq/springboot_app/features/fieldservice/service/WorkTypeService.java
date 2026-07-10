package com.procureiq.springboot_app.features.fieldservice.service;

import com.procureiq.springboot_app.features.fieldservice.dto.WorkTypeRequest;
import com.procureiq.springboot_app.features.fieldservice.dto.WorkTypeResponse;
import com.procureiq.springboot_app.features.fieldservice.entity.WorkType;
import com.procureiq.springboot_app.features.fieldservice.repository.WorkTypeRepository;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WorkTypeService {

    private final WorkTypeRepository workTypeRepository;
    private final Tracer tracer = GlobalOpenTelemetry.getTracer("springboot-app", "1.0.0");

    public WorkTypeService(WorkTypeRepository workTypeRepository) {
        this.workTypeRepository = workTypeRepository;
    }

    @Transactional
    public WorkTypeResponse createWorkType(WorkTypeRequest request) {
        Span span = tracer.spanBuilder("WorkTypeService.createWorkType").startSpan();
        try {
            WorkType wt = new WorkType();
            wt.setName(request.name());
            wt.setDefaultDurationMinutes(request.defaultDurationMinutes() != null ? request.defaultDurationMinutes() : 60);
            wt.setEstimatedTravelMinutes(request.estimatedTravelMinutes() != null ? request.estimatedTravelMinutes() : 0);
            wt = workTypeRepository.save(wt);
            return new WorkTypeResponse(wt.getId(), wt.getName(), wt.getDefaultDurationMinutes(), wt.getEstimatedTravelMinutes());
        } finally {
            span.end();
        }
    }

    @Transactional(readOnly = true)
    public WorkTypeResponse getWorkType(Long id) {
        Span span = tracer.spanBuilder("WorkTypeService.getWorkType").startSpan();
        try {
            WorkType wt = workTypeRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("WorkType not found: " + id));
            return new WorkTypeResponse(wt.getId(), wt.getName(), wt.getDefaultDurationMinutes(), wt.getEstimatedTravelMinutes());
        } finally {
            span.end();
        }
    }

    @Transactional
    public WorkTypeResponse updateWorkType(Long id, WorkTypeRequest request) {
        Span span = tracer.spanBuilder("WorkTypeService.updateWorkType").startSpan();
        try {
            WorkType wt = workTypeRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("WorkType not found: " + id));
            wt.setName(request.name());
            wt.setDefaultDurationMinutes(request.defaultDurationMinutes() != null ? request.defaultDurationMinutes() : 60);
            wt.setEstimatedTravelMinutes(request.estimatedTravelMinutes() != null ? request.estimatedTravelMinutes() : 0);
            wt = workTypeRepository.save(wt);
            return new WorkTypeResponse(wt.getId(), wt.getName(), wt.getDefaultDurationMinutes(), wt.getEstimatedTravelMinutes());
        } finally {
            span.end();
        }
    }

    @Transactional
    public void deleteWorkType(Long id) {
        Span span = tracer.spanBuilder("WorkTypeService.deleteWorkType").startSpan();
        try {
            workTypeRepository.deleteById(id);
        } finally {
            span.end();
        }
    }
}
