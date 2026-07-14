package com.procureiq.springboot_app.features.fieldservice.service;

import com.procureiq.springboot_app.features.fieldservice.dto.request.*;
import com.procureiq.springboot_app.features.fieldservice.dto.response.*;
import com.procureiq.springboot_app.features.fieldservice.entity.AssignedResource;
import com.procureiq.springboot_app.features.fieldservice.entity.ServiceAppointment;
import com.procureiq.springboot_app.features.fieldservice.repository.*;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServiceAppointmentService {

    @Autowired
    private ServiceAppointmentRepository serviceAppointmentRepository;
    @Autowired
    private ServiceResourceRepository serviceResourceRepository;
    @Autowired
    private AssignedResourceRepository assignedResourceRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ContactRepository contactRepository;
    @Autowired
    private ServiceTerritoryRepository serviceTerritoryRepository;
    @Autowired
    private WorkTypeRepository workTypeRepository;
    @Autowired
    private ServiceCrewRepository serviceCrewRepository;

    private final Tracer tracer = GlobalOpenTelemetry.getTracer("springboot-app", "1.0.0");

    public ServiceAppointmentService() {}

    @Transactional(readOnly = true)
    public java.util.List<ServiceAppointmentResponse> getAllServiceAppointments() {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeServiceWithTracing(() -> {
            return serviceAppointmentRepository.findAll().stream()
                    .map(ServiceAppointmentResponse::fromEntity)
                    .collect(java.util.stream.Collectors.toList());
        });
    }

    @Transactional
    public ServiceAppointmentResponse createServiceAppointment(ServiceAppointmentRequest request) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeServiceWithTracing(() -> {
            ServiceAppointment sa = new ServiceAppointment();
            sa.setParentRecordType(request.parentRecordType() != null ? request.parentRecordType() : "work_order");
            sa.setParentRecordId(request.parentRecordId());
            if (request.accountId() != null) {
                sa.setAccount(accountRepository.findById(request.accountId()).orElse(null));
            }
            if (request.contactId() != null) {
                sa.setContact(contactRepository.findById(request.contactId()).orElse(null));
            }
            if (request.serviceTerritoryId() != null) {
                sa.setServiceTerritory(serviceTerritoryRepository.findById(request.serviceTerritoryId()).orElse(null));
            }
            if (request.workTypeId() != null) {
                sa.setWorkType(workTypeRepository.findById(request.workTypeId()).orElse(null));
            }
            if (request.status() != null) {
                sa.setStatus(request.status());
            }
            sa.setScheduledStart(request.scheduledStart());
            sa.setScheduledEnd(request.scheduledEnd());
            sa.setArrivalWindowStart(request.arrivalWindowStart());
            sa.setArrivalWindowEnd(request.arrivalWindowEnd());
            sa.setDurationMinutes(request.durationMinutes());
            sa.setAddress(request.address());

            sa = serviceAppointmentRepository.save(sa);
            return ServiceAppointmentResponse.fromEntity(sa);
        });
    }

    @Transactional(readOnly = true)
    public ServiceAppointmentResponse getServiceAppointment(Long id) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeServiceWithTracing(() -> {
            ServiceAppointment sa = serviceAppointmentRepository.findFirstById(id)
                    .orElseThrow(() -> new IllegalArgumentException("ServiceAppointment not found: " + id));
            return ServiceAppointmentResponse.fromEntity(sa);
        });
    }

    @Transactional
    public ServiceAppointmentResponse updateServiceAppointment(Long id, ServiceAppointmentRequest request) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeServiceWithTracing(() -> {
            ServiceAppointment sa = serviceAppointmentRepository.findFirstById(id)
                    .orElseThrow(() -> new IllegalArgumentException("ServiceAppointment not found: " + id));
            if (request.parentRecordType() != null) {
                sa.setParentRecordType(request.parentRecordType());
            }
            if (request.parentRecordId() != null) {
                sa.setParentRecordId(request.parentRecordId());
            }
            if (request.accountId() != null) {
                sa.setAccount(accountRepository.findById(request.accountId()).orElse(null));
            }
            if (request.contactId() != null) {
                sa.setContact(contactRepository.findById(request.contactId()).orElse(null));
            }
            if (request.serviceTerritoryId() != null) {
                sa.setServiceTerritory(serviceTerritoryRepository.findById(request.serviceTerritoryId()).orElse(null));
            }
            if (request.workTypeId() != null) {
                sa.setWorkType(workTypeRepository.findById(request.workTypeId()).orElse(null));
            }
            if (request.status() != null) {
                sa.setStatus(request.status());
            }
            if (request.scheduledStart() != null) {
                sa.setScheduledStart(request.scheduledStart());
            }
            if (request.scheduledEnd() != null) {
                sa.setScheduledEnd(request.scheduledEnd());
            }
            if (request.arrivalWindowStart() != null) {
                sa.setArrivalWindowStart(request.arrivalWindowStart());
            }
            if (request.arrivalWindowEnd() != null) {
                sa.setArrivalWindowEnd(request.arrivalWindowEnd());
            }
            if (request.durationMinutes() != null) {
                sa.setDurationMinutes(request.durationMinutes());
            }
            if (request.address() != null) {
                sa.setAddress(request.address());
            }
            sa = serviceAppointmentRepository.save(sa);
            return ServiceAppointmentResponse.fromEntity(sa);
        });
    }

    @Transactional
    public void deleteServiceAppointment(Long id) {
        com.procureiq.springboot_app.infra.config.TracingHelper.executeServiceVoidWithTracing(() -> {
            ServiceAppointment sa = serviceAppointmentRepository.findFirstById(id)
                    .orElseThrow(() -> new IllegalArgumentException("ServiceAppointment not found: " + id));
            serviceAppointmentRepository.delete(sa);
        });
    }

    @Transactional
    public AssignedResourceResponse assignResource(Long appointmentId, AssignResourceRequest request) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeServiceWithTracing(() -> {
            ServiceAppointment sa = serviceAppointmentRepository.findFirstById(appointmentId)
                    .orElseThrow(() -> new IllegalArgumentException("ServiceAppointment not found: " + appointmentId));

            AssignedResource ar = new AssignedResource();
            ar.setServiceAppointmentId(sa.getId());
            ar.setServiceAppointmentCreatedAt(sa.getCreatedAt());
            if (request.serviceResourceId() != null) {
                ar.setServiceResource(serviceResourceRepository.findById(request.serviceResourceId()).orElse(null));
            }
            if (request.serviceCrewId() != null) {
                ar.setServiceCrew(serviceCrewRepository.findById(request.serviceCrewId()).orElse(null));
            }
            if (request.isPrimaryResource() != null) {
                ar.setIsPrimaryResource(request.isPrimaryResource());
            }
            if (request.status() != null) {
                ar.setStatus(request.status());
            }
            ar = assignedResourceRepository.save(ar);

            return new AssignedResourceResponse(
                    ar.getId(),
                    ar.getServiceAppointmentId(),
                    ar.getServiceAppointmentCreatedAt(),
                    ar.getServiceResource() != null ? ar.getServiceResource().getId() : null,
                    ar.getServiceCrew() != null ? ar.getServiceCrew().getId() : null,
                    ar.getIsPrimaryResource(),
                    ar.getAssignedAt(),
                    ar.getStatus()
            );
        });
    }

    @Transactional
    public void deleteAssignedResource(Long id) {
        com.procureiq.springboot_app.infra.config.TracingHelper.executeServiceVoidWithTracing(() -> {
            assignedResourceRepository.deleteById(id);
        });
    }
}
