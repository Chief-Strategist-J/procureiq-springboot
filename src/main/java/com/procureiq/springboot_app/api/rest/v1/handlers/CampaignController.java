package com.procureiq.springboot_app.api.rest.v1.handlers;

import com.procureiq.springboot_app.features.campaigns.dto.*;
import com.procureiq.springboot_app.features.campaigns.service.CampaignService;
import com.procureiq.springboot_app.shared.types.ApiResponse;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.CAMPAIGNS)
public class CampaignController {

    private final CampaignService campaignService;
    private final Tracer tracer = GlobalOpenTelemetry.getTracer("springboot-app", "1.0.0");

    public CampaignController(CampaignService campaignService) {
        this.campaignService = campaignService;
    }

    // --- Campaign Endpoints ---

    @GetMapping
    public ResponseEntity<?> getAllCampaigns() {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            List<CampaignResponse> response = campaignService.getAllCampaigns();
            return ResponseEntity.ok(ApiResponse.success(200, response));
        });
    }

    @PostMapping
    public ResponseEntity<?> createCampaign(@RequestBody CampaignRequest request) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            CampaignResponse response = campaignService.createCampaign(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(201, response));
        });
    }

    @GetMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.PATH_ID)
    public ResponseEntity<?> getCampaign(@PathVariable Long id) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            CampaignResponse response = campaignService.getCampaign(id);
            return ResponseEntity.ok(ApiResponse.success(200, response));
        });
    }

    @PutMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.PATH_ID)
    public ResponseEntity<?> updateCampaign(@PathVariable Long id, @RequestBody CampaignRequest request) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            CampaignResponse response = campaignService.updateCampaign(id, request);
            return ResponseEntity.ok(ApiResponse.success(200, response));
        });
    }

    @DeleteMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.PATH_ID)
    public ResponseEntity<?> deleteCampaign(@PathVariable Long id) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            campaignService.deleteCampaign(id);
            return ResponseEntity.ok(ApiResponse.success(200, "Deleted campaign successfully"));
        });
    }

    // --- Scheduled Email Endpoints ---

    @GetMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.SCHEDULES)
    public ResponseEntity<?> getAllScheduledEmails() {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            List<ScheduledEmailResponse> response = campaignService.getAllScheduledEmails();
            return ResponseEntity.ok(ApiResponse.success(200, response));
        });
    }

    @PostMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.SCHEDULES)
    public ResponseEntity<?> createScheduledEmail(@RequestBody ScheduledEmailRequest request) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            ScheduledEmailResponse response = campaignService.createScheduledEmail(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(201, response));
        });
    }

    @GetMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.SCHEDULES_ID)
    public ResponseEntity<?> getScheduledEmail(@PathVariable Long id) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            ScheduledEmailResponse response = campaignService.getScheduledEmail(id);
            return ResponseEntity.ok(ApiResponse.success(200, response));
        });
    }

    @PutMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.SCHEDULES_ID)
    public ResponseEntity<?> updateScheduledEmail(@PathVariable Long id, @RequestBody ScheduledEmailRequest request) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            ScheduledEmailResponse response = campaignService.updateScheduledEmail(id, request);
            return ResponseEntity.ok(ApiResponse.success(200, response));
        });
    }

    @DeleteMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.SCHEDULES_ID)
    public ResponseEntity<?> deleteScheduledEmail(@PathVariable Long id) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            campaignService.deleteScheduledEmail(id);
            return ResponseEntity.ok(ApiResponse.success(200, "Deleted scheduled email successfully"));
        });
    }

    // --- Recipient Endpoints ---

    @GetMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.RECIPIENTS)
    public ResponseEntity<?> getAllRecipients() {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            List<RecipientResponse> response = campaignService.getAllRecipients();
            return ResponseEntity.ok(ApiResponse.success(200, response));
        });
    }

    @PostMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.RECIPIENTS)
    public ResponseEntity<?> createRecipient(@RequestBody RecipientRequest request) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            RecipientResponse response = campaignService.createRecipient(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(201, response));
        });
    }

    @GetMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.RECIPIENTS_ID)
    public ResponseEntity<?> getRecipient(@PathVariable Long id) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            RecipientResponse response = campaignService.getRecipient(id);
            return ResponseEntity.ok(ApiResponse.success(200, response));
        });
    }

    @PutMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.RECIPIENTS_ID)
    public ResponseEntity<?> updateRecipient(@PathVariable Long id, @RequestBody RecipientRequest request) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            RecipientResponse response = campaignService.updateRecipient(id, request);
            return ResponseEntity.ok(ApiResponse.success(200, response));
        });
    }

    @DeleteMapping(com.procureiq.springboot_app.infra.config.ApiEndpoints.RECIPIENTS_ID)
    public ResponseEntity<?> deleteRecipient(@PathVariable Long id) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeWithTracing(() -> {
            campaignService.deleteRecipient(id);
            return ResponseEntity.ok(ApiResponse.success(200, "Deleted recipient successfully"));
        });
    }
}
