package com.procureiq.springboot_app.features.fieldservice.service;

import com.procureiq.springboot_app.features.fieldservice.dto.request.ServiceTerritoryMemberRequest;
import com.procureiq.springboot_app.features.fieldservice.dto.response.ServiceTerritoryMemberResponse;
import com.procureiq.springboot_app.features.fieldservice.entity.OperatingHours;
import com.procureiq.springboot_app.features.fieldservice.entity.ServiceResource;
import com.procureiq.springboot_app.features.fieldservice.entity.ServiceTerritory;
import com.procureiq.springboot_app.features.fieldservice.entity.ServiceTerritoryMember;
import com.procureiq.springboot_app.features.fieldservice.repository.OperatingHoursRepository;
import com.procureiq.springboot_app.features.fieldservice.repository.ServiceResourceRepository;
import com.procureiq.springboot_app.features.fieldservice.repository.ServiceTerritoryMemberRepository;
import com.procureiq.springboot_app.features.fieldservice.repository.ServiceTerritoryRepository;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServiceTerritoryMemberService {

    private final ServiceTerritoryMemberRepository serviceTerritoryMemberRepository;
    private final ServiceTerritoryRepository serviceTerritoryRepository;
    private final ServiceResourceRepository serviceResourceRepository;
    private final OperatingHoursRepository operatingHoursRepository;
    private final Tracer tracer = GlobalOpenTelemetry.getTracer("springboot-app", "1.0.0");

    public ServiceTerritoryMemberService(ServiceTerritoryMemberRepository serviceTerritoryMemberRepository,
                                         ServiceTerritoryRepository serviceTerritoryRepository,
                                         ServiceResourceRepository serviceResourceRepository,
                                         OperatingHoursRepository operatingHoursRepository) {
        this.serviceTerritoryMemberRepository = serviceTerritoryMemberRepository;
        this.serviceTerritoryRepository = serviceTerritoryRepository;
        this.serviceResourceRepository = serviceResourceRepository;
        this.operatingHoursRepository = operatingHoursRepository;
    }

    @Transactional
    public ServiceTerritoryMemberResponse createServiceTerritoryMember(ServiceTerritoryMemberRequest request) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeServiceWithTracing(() -> {
            ServiceTerritory st = serviceTerritoryRepository.findById(request.serviceTerritoryId())
                    .orElseThrow(() -> new IllegalArgumentException("ServiceTerritory not found: " + request.serviceTerritoryId()));
            ServiceResource sr = serviceResourceRepository.findById(request.serviceResourceId())
                    .orElseThrow(() -> new IllegalArgumentException("ServiceResource not found: " + request.serviceResourceId()));

            OperatingHours oh = null;
            if (request.operatingHoursId() != null) {
                oh = operatingHoursRepository.findById(request.operatingHoursId())
                        .orElseThrow(() -> new IllegalArgumentException("OperatingHours not found: " + request.operatingHoursId()));
            }

            ServiceTerritoryMember stm = new ServiceTerritoryMember();
            stm.setServiceTerritory(st);
            stm.setServiceResource(sr);
            stm.setOperatingHours(oh);
            stm.setTerritoryType(request.territoryType() != null ? request.territoryType() : "primary");
            stm.setTravelMode(request.travelMode() != null ? request.travelMode() : "driving");
            stm = serviceTerritoryMemberRepository.save(stm);

            return mapToResponse(stm);
        });
    }

    @Transactional(readOnly = true)
    public ServiceTerritoryMemberResponse getServiceTerritoryMember(Long id) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeServiceWithTracing(() -> {
            ServiceTerritoryMember stm = serviceTerritoryMemberRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("ServiceTerritoryMember not found: " + id));
            return mapToResponse(stm);
        });
    }

    @Transactional
    public ServiceTerritoryMemberResponse updateServiceTerritoryMember(Long id, ServiceTerritoryMemberRequest request) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeServiceWithTracing(() -> {
            ServiceTerritoryMember stm = serviceTerritoryMemberRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("ServiceTerritoryMember not found: " + id));

            ServiceTerritory st = serviceTerritoryRepository.findById(request.serviceTerritoryId())
                    .orElseThrow(() -> new IllegalArgumentException("ServiceTerritory not found: " + request.serviceTerritoryId()));
            ServiceResource sr = serviceResourceRepository.findById(request.serviceResourceId())
                    .orElseThrow(() -> new IllegalArgumentException("ServiceResource not found: " + request.serviceResourceId()));

            OperatingHours oh = null;
            if (request.operatingHoursId() != null) {
                oh = operatingHoursRepository.findById(request.operatingHoursId())
                        .orElseThrow(() -> new IllegalArgumentException("OperatingHours not found: " + request.operatingHoursId()));
            }

            stm.setServiceTerritory(st);
            stm.setServiceResource(sr);
            stm.setOperatingHours(oh);
            if (request.territoryType() != null) {
                stm.setTerritoryType(request.territoryType());
            }
            if (request.travelMode() != null) {
                stm.setTravelMode(request.travelMode());
            }
            stm = serviceTerritoryMemberRepository.save(stm);

            return mapToResponse(stm);
        });
    }

    @Transactional
    public void deleteServiceTerritoryMember(Long id) {
        com.procureiq.springboot_app.infra.config.TracingHelper.executeServiceVoidWithTracing(() -> {
            serviceTerritoryMemberRepository.deleteById(id);
        });
    }

    private ServiceTerritoryMemberResponse mapToResponse(ServiceTerritoryMember stm) {
        return new ServiceTerritoryMemberResponse(
            stm.getId(),
            stm.getServiceTerritory().getId(),
            stm.getServiceResource().getId(),
            stm.getOperatingHours() != null ? stm.getOperatingHours().getId() : null,
            stm.getTerritoryType(),
            stm.getTravelMode()
        );
    }
}
