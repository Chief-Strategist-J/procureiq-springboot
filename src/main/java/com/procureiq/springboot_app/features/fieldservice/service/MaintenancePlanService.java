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
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeServiceWithTracing(() -> {
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
        });
    }

    @Transactional(readOnly = true)
    public MaintenancePlanResponse getMaintenancePlan(Long id) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeServiceWithTracing(() -> {
            MaintenancePlan mp = maintenancePlanRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("MaintenancePlan not found: " + id));
            return mapToResponse(mp);
        });
    }

    @Transactional
    public MaintenancePlanResponse updateMaintenancePlan(Long id, MaintenancePlanRequest request) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeServiceWithTracing(() -> {
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
            if (request.frequency() != null) {
                mp.setFrequency(request.frequency());
            }
            if (request.generationLeadDays() != null) {
                mp.setGenerationLeadDays(request.generationLeadDays());
            }
            if (request.nextGenerationDate() != null) {
                mp.setNextGenerationDate(request.nextGenerationDate());
            }
            if (request.isActive() != null) {
                mp.setIsActive(request.isActive());
            }
            mp = maintenancePlanRepository.save(mp);

            return mapToResponse(mp);
        });
    }

    @Transactional
    public void deleteMaintenancePlan(Long id) {
        com.procureiq.springboot_app.infra.config.TracingHelper.executeServiceVoidWithTracing(() -> {
            maintenancePlanRepository.deleteById(id);
        });
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
