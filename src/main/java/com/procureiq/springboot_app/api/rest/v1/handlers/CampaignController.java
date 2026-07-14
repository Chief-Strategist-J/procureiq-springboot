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
        Span span = tracer.spanBuilder("REST.getAllCampaigns").startSpan();
        try (Scope scope = span.makeCurrent()) {
            List<CampaignResponse> response = campaignService.getAllCampaigns();
            span.setStatus(StatusCode.OK);
            return ResponseEntity.ok(ApiResponse.success(200, response));
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(500, e.getMessage()));
        } finally {
            span.end();
        }
    }

    @PostMapping
    public ResponseEntity<?> createCampaign(@RequestBody CampaignRequest request) {
        Span span = tracer.spanBuilder("REST.createCampaign").startSpan();
        try (Scope scope = span.makeCurrent()) {
            CampaignResponse response = campaignService.createCampaign(request);
            span.setStatus(StatusCode.OK);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(201, response));
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(400, e.getMessage()));
        } finally {
            span.end();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCampaign(@PathVariable Long id) {
        Span span = tracer.spanBuilder("REST.getCampaign").startSpan();
        try (Scope scope = span.makeCurrent()) {
            CampaignResponse response = campaignService.getCampaign(id);
            span.setStatus(StatusCode.OK);
            return ResponseEntity.ok(ApiResponse.success(200, response));
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(400, e.getMessage()));
        } finally {
            span.end();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCampaign(@PathVariable Long id, @RequestBody CampaignRequest request) {
        Span span = tracer.spanBuilder("REST.updateCampaign").startSpan();
        try (Scope scope = span.makeCurrent()) {
            CampaignResponse response = campaignService.updateCampaign(id, request);
            span.setStatus(StatusCode.OK);
            return ResponseEntity.ok(ApiResponse.success(200, response));
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(400, e.getMessage()));
        } finally {
            span.end();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCampaign(@PathVariable Long id) {
        Span span = tracer.spanBuilder("REST.deleteCampaign").startSpan();
        try (Scope scope = span.makeCurrent()) {
            campaignService.deleteCampaign(id);
            span.setStatus(StatusCode.OK);
            return ResponseEntity.ok(ApiResponse.success(200, "Deleted campaign successfully"));
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(400, e.getMessage()));
        } finally {
            span.end();
        }
    }

    // --- Scheduled Email Endpoints ---

    @GetMapping("/schedules")
    public ResponseEntity<?> getAllScheduledEmails() {
        Span span = tracer.spanBuilder("REST.getAllScheduledEmails").startSpan();
        try (Scope scope = span.makeCurrent()) {
            List<ScheduledEmailResponse> response = campaignService.getAllScheduledEmails();
            span.setStatus(StatusCode.OK);
            return ResponseEntity.ok(ApiResponse.success(200, response));
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(500, e.getMessage()));
        } finally {
            span.end();
        }
    }

    @PostMapping("/schedules")
    public ResponseEntity<?> createScheduledEmail(@RequestBody ScheduledEmailRequest request) {
        Span span = tracer.spanBuilder("REST.createScheduledEmail").startSpan();
        try (Scope scope = span.makeCurrent()) {
            ScheduledEmailResponse response = campaignService.createScheduledEmail(request);
            span.setStatus(StatusCode.OK);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(201, response));
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(400, e.getMessage()));
        } finally {
            span.end();
        }
    }

    @GetMapping("/schedules/{id}")
    public ResponseEntity<?> getScheduledEmail(@PathVariable Long id) {
        Span span = tracer.spanBuilder("REST.getScheduledEmail").startSpan();
        try (Scope scope = span.makeCurrent()) {
            ScheduledEmailResponse response = campaignService.getScheduledEmail(id);
            span.setStatus(StatusCode.OK);
            return ResponseEntity.ok(ApiResponse.success(200, response));
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(400, e.getMessage()));
        } finally {
            span.end();
        }
    }

    @PutMapping("/schedules/{id}")
    public ResponseEntity<?> updateScheduledEmail(@PathVariable Long id, @RequestBody ScheduledEmailRequest request) {
        Span span = tracer.spanBuilder("REST.updateScheduledEmail").startSpan();
        try (Scope scope = span.makeCurrent()) {
            ScheduledEmailResponse response = campaignService.updateScheduledEmail(id, request);
            span.setStatus(StatusCode.OK);
            return ResponseEntity.ok(ApiResponse.success(200, response));
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(400, e.getMessage()));
        } finally {
            span.end();
        }
    }

    @DeleteMapping("/schedules/{id}")
    public ResponseEntity<?> deleteScheduledEmail(@PathVariable Long id) {
        Span span = tracer.spanBuilder("REST.deleteScheduledEmail").startSpan();
        try (Scope scope = span.makeCurrent()) {
            campaignService.deleteScheduledEmail(id);
            span.setStatus(StatusCode.OK);
            return ResponseEntity.ok(ApiResponse.success(200, "Deleted scheduled email successfully"));
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(400, e.getMessage()));
        } finally {
            span.end();
        }
    }

    // --- Recipient Endpoints ---

    @GetMapping("/recipients")
    public ResponseEntity<?> getAllRecipients() {
        Span span = tracer.spanBuilder("REST.getAllRecipients").startSpan();
        try (Scope scope = span.makeCurrent()) {
            List<RecipientResponse> response = campaignService.getAllRecipients();
            span.setStatus(StatusCode.OK);
            return ResponseEntity.ok(ApiResponse.success(200, response));
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(500, e.getMessage()));
        } finally {
            span.end();
        }
    }

    @PostMapping("/recipients")
    public ResponseEntity<?> createRecipient(@RequestBody RecipientRequest request) {
        Span span = tracer.spanBuilder("REST.createRecipient").startSpan();
        try (Scope scope = span.makeCurrent()) {
            RecipientResponse response = campaignService.createRecipient(request);
            span.setStatus(StatusCode.OK);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(201, response));
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(400, e.getMessage()));
        } finally {
            span.end();
        }
    }

    @GetMapping("/recipients/{id}")
    public ResponseEntity<?> getRecipient(@PathVariable Long id) {
        Span span = tracer.spanBuilder("REST.getRecipient").startSpan();
        try (Scope scope = span.makeCurrent()) {
            RecipientResponse response = campaignService.getRecipient(id);
            span.setStatus(StatusCode.OK);
            return ResponseEntity.ok(ApiResponse.success(200, response));
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(400, e.getMessage()));
        } finally {
            span.end();
        }
    }

    @PutMapping("/recipients/{id}")
    public ResponseEntity<?> updateRecipient(@PathVariable Long id, @RequestBody RecipientRequest request) {
        Span span = tracer.spanBuilder("REST.updateRecipient").startSpan();
        try (Scope scope = span.makeCurrent()) {
            RecipientResponse response = campaignService.updateRecipient(id, request);
            span.setStatus(StatusCode.OK);
            return ResponseEntity.ok(ApiResponse.success(200, response));
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(400, e.getMessage()));
        } finally {
            span.end();
        }
    }

    @DeleteMapping("/recipients/{id}")
    public ResponseEntity<?> deleteRecipient(@PathVariable Long id) {
        Span span = tracer.spanBuilder("REST.deleteRecipient").startSpan();
        try (Scope scope = span.makeCurrent()) {
            campaignService.deleteRecipient(id);
            span.setStatus(StatusCode.OK);
            return ResponseEntity.ok(ApiResponse.success(200, "Deleted recipient successfully"));
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(400, e.getMessage()));
        } finally {
            span.end();
        }
    }
}
