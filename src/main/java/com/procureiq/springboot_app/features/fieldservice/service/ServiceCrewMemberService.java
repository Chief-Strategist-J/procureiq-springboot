package com.procureiq.springboot_app.features.fieldservice.service;

import com.procureiq.springboot_app.features.fieldservice.dto.ServiceCrewMemberRequest;
import com.procureiq.springboot_app.features.fieldservice.dto.ServiceCrewMemberResponse;
import com.procureiq.springboot_app.features.fieldservice.entity.ServiceCrew;
import com.procureiq.springboot_app.features.fieldservice.entity.ServiceCrewMember;
import com.procureiq.springboot_app.features.fieldservice.entity.ServiceResource;
import com.procureiq.springboot_app.features.fieldservice.repository.ServiceCrewMemberRepository;
import com.procureiq.springboot_app.features.fieldservice.repository.ServiceCrewRepository;
import com.procureiq.springboot_app.features.fieldservice.repository.ServiceResourceRepository;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServiceCrewMemberService {

    private final ServiceCrewMemberRepository serviceCrewMemberRepository;
    private final ServiceCrewRepository serviceCrewRepository;
    private final ServiceResourceRepository serviceResourceRepository;
    private final Tracer tracer = GlobalOpenTelemetry.getTracer("springboot-app", "1.0.0");

    public ServiceCrewMemberService(ServiceCrewMemberRepository serviceCrewMemberRepository,
                                    ServiceCrewRepository serviceCrewRepository,
                                    ServiceResourceRepository serviceResourceRepository) {
        this.serviceCrewMemberRepository = serviceCrewMemberRepository;
        this.serviceCrewRepository = serviceCrewRepository;
        this.serviceResourceRepository = serviceResourceRepository;
    }

    @Transactional
    public ServiceCrewMemberResponse createServiceCrewMember(ServiceCrewMemberRequest request) {
        Span span = tracer.spanBuilder("ServiceCrewMemberService.createServiceCrewMember").startSpan();
        try {
            ServiceCrew sc = serviceCrewRepository.findById(request.serviceCrewId())
                    .orElseThrow(() -> new IllegalArgumentException("ServiceCrew not found: " + request.serviceCrewId()));
            ServiceResource sr = serviceResourceRepository.findById(request.serviceResourceId())
                    .orElseThrow(() -> new IllegalArgumentException("ServiceResource not found: " + request.serviceResourceId()));

            ServiceCrewMember scm = new ServiceCrewMember();
            scm.setServiceCrew(sc);
            scm.setServiceResource(sr);
            scm.setMemberRole(request.memberRole() != null ? request.memberRole() : "member");
            scm.setStartDate(request.startDate() != null ? request.startDate() : java.time.LocalDate.now());
            scm.setEndDate(request.endDate());
            scm = serviceCrewMemberRepository.save(scm);

            return mapToResponse(scm);
        } finally {
            span.end();
        }
    }

    @Transactional(readOnly = true)
    public ServiceCrewMemberResponse getServiceCrewMember(Long id) {
        Span span = tracer.spanBuilder("ServiceCrewMemberService.getServiceCrewMember").startSpan();
        try {
            ServiceCrewMember scm = serviceCrewMemberRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("ServiceCrewMember not found: " + id));
            return mapToResponse(scm);
        } finally {
            span.end();
        }
    }

    @Transactional
    public ServiceCrewMemberResponse updateServiceCrewMember(Long id, ServiceCrewMemberRequest request) {
        Span span = tracer.spanBuilder("ServiceCrewMemberService.updateServiceCrewMember").startSpan();
        try {
            ServiceCrewMember scm = serviceCrewMemberRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("ServiceCrewMember not found: " + id));

            ServiceCrew sc = serviceCrewRepository.findById(request.serviceCrewId())
                    .orElseThrow(() -> new IllegalArgumentException("ServiceCrew not found: " + request.serviceCrewId()));
            ServiceResource sr = serviceResourceRepository.findById(request.serviceResourceId())
                    .orElseThrow(() -> new IllegalArgumentException("ServiceResource not found: " + request.serviceResourceId()));

            scm.setServiceCrew(sc);
            scm.setServiceResource(sr);
            scm.setMemberRole(request.memberRole());
            scm.setStartDate(request.startDate());
            scm.setEndDate(request.endDate());
            scm = serviceCrewMemberRepository.save(scm);

            return mapToResponse(scm);
        } finally {
            span.end();
        }
    }

    @Transactional
    public void deleteServiceCrewMember(Long id) {
        Span span = tracer.spanBuilder("ServiceCrewMemberService.deleteServiceCrewMember").startSpan();
        try {
            serviceCrewMemberRepository.deleteById(id);
        } finally {
            span.end();
        }
    }

    private ServiceCrewMemberResponse mapToResponse(ServiceCrewMember scm) {
        return new ServiceCrewMemberResponse(
            scm.getId(),
            scm.getServiceCrew().getId(),
            scm.getServiceResource().getId(),
            scm.getMemberRole(),
            scm.getStartDate(),
            scm.getEndDate()
        );
    }
}
