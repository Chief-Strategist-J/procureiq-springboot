package com.procureiq.springboot_app.features.fieldservice.service;

import com.procureiq.springboot_app.features.fieldservice.dto.request.MaintenancePlanRequest;
import com.procureiq.springboot_app.features.fieldservice.dto.response.MaintenancePlanResponse;
import com.procureiq.springboot_app.features.fieldservice.entity.Account;
import com.procureiq.springboot_app.features.fieldservice.entity.Asset;
import com.procureiq.springboot_app.features.fieldservice.entity.MaintenancePlan;
import com.procureiq.springboot_app.features.fieldservice.entity.WorkType;
import com.procureiq.springboot_app.features.fieldservice.repository.AccountRepository;
import com.procureiq.springboot_app.features.fieldservice.repository.AssetRepository;
import com.procureiq.springboot_app.features.fieldservice.repository.MaintenancePlanRepository;
import com.procureiq.springboot_app.features.fieldservice.repository.WorkTypeRepository;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MaintenancePlanService {

    private final MaintenancePlanRepository maintenancePlanRepository;
    private final AccountRepository accountRepository;
    private final AssetRepository assetRepository;
    private final WorkTypeRepository workTypeRepository;
    private final Tracer tracer = GlobalOpenTelemetry.getTracer("springboot-app", "1.0.0");

    public MaintenancePlanService(MaintenancePlanRepository maintenancePlanRepository,
                                  AccountRepository accountRepository,
                                  AssetRepository assetRepository,
                                  WorkTypeRepository workTypeRepository) {
        this.maintenancePlanRepository = maintenancePlanRepository;
        this.accountRepository = accountRepository;
        this.assetRepository = assetRepository;
        this.workTypeRepository = workTypeRepository;
    }

    @Transactional
    public MaintenancePlanResponse createMaintenancePlan(MaintenancePlanRequest request) {
        Span span = tracer.spanBuilder("MaintenancePlanService.createMaintenancePlan").startSpan();
        try {
            Account account = accountRepository.findById(request.accountId())
                    .orElseThrow(() -> new IllegalArgumentException("Account not found: " + request.accountId()));

            Asset asset = null;
            if (request.assetId() != null) {
                asset = assetRepository.findById(request.assetId())
                        .orElseThrow(() -> new IllegalArgumentException("Asset not found: " + request.assetId()));
            }

            WorkType wt = null;
            if (request.workTypeId() != null) {
                wt = workTypeRepository.findById(request.workTypeId())
                        .orElseThrow(() -> new IllegalArgumentException("WorkType not found: " + request.workTypeId()));
            }

            MaintenancePlan mp = new MaintenancePlan();
            mp.setAccount(account);
            mp.setAsset(asset);
            mp.setWorkType(wt);
            mp.setFrequency(request.frequency());
            mp.setGenerationLeadDays(request.generationLeadDays() != null ? request.generationLeadDays() : 14);
            mp.setNextGenerationDate(request.nextGenerationDate());
            mp.setIsActive(request.isActive() != null ? request.isActive() : true);
            mp = maintenancePlanRepository.save(mp);

            return mapToResponse(mp);
        } finally {
            span.end();
        }
    }

    @Transactional(readOnly = true)
    public MaintenancePlanResponse getMaintenancePlan(Long id) {
        Span span = tracer.spanBuilder("MaintenancePlanService.getMaintenancePlan").startSpan();
        try {
            MaintenancePlan mp = maintenancePlanRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("MaintenancePlan not found: " + id));
            return mapToResponse(mp);
        } finally {
            span.end();
        }
    }

    @Transactional
    public MaintenancePlanResponse updateMaintenancePlan(Long id, MaintenancePlanRequest request) {
        Span span = tracer.spanBuilder("MaintenancePlanService.updateMaintenancePlan").startSpan();
        try {
            MaintenancePlan mp = maintenancePlanRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("MaintenancePlan not found: " + id));

            Account account = accountRepository.findById(request.accountId())
                    .orElseThrow(() -> new IllegalArgumentException("Account not found: " + request.accountId()));

            Asset asset = null;
            if (request.assetId() != null) {
                asset = assetRepository.findById(request.assetId())
                        .orElseThrow(() -> new IllegalArgumentException("Asset not found: " + request.assetId()));
            }

            WorkType wt = null;
            if (request.workTypeId() != null) {
                wt = workTypeRepository.findById(request.workTypeId())
                        .orElseThrow(() -> new IllegalArgumentException("WorkType not found: " + request.workTypeId()));
            }

            mp.setAccount(account);
            mp.setAsset(asset);
            mp.setWorkType(wt);
            mp.setFrequency(request.frequency());
            mp.setGenerationLeadDays(request.generationLeadDays());
            mp.setNextGenerationDate(request.nextGenerationDate());
            mp.setIsActive(request.isActive());
            mp = maintenancePlanRepository.save(mp);

            return mapToResponse(mp);
        } finally {
            span.end();
        }
    }

    @Transactional
    public void deleteMaintenancePlan(Long id) {
        Span span = tracer.spanBuilder("MaintenancePlanService.deleteMaintenancePlan").startSpan();
        try {
            maintenancePlanRepository.deleteById(id);
        } finally {
            span.end();
        }
    }

    private MaintenancePlanResponse mapToResponse(MaintenancePlan mp) {
        return new MaintenancePlanResponse(
            mp.getId(),
            mp.getAccount().getId(),
            mp.getAsset() != null ? mp.getAsset().getId() : null,
            mp.getWorkType() != null ? mp.getWorkType().getId() : null,
            mp.getFrequency(),
            mp.getGenerationLeadDays(),
            mp.getNextGenerationDate(),
            mp.getIsActive()
        );
    }
}
