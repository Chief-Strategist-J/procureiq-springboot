package com.procureiq.springboot_app.api.rest.v1.handlers;

import com.procureiq.springboot_app.features.identity.dto.request.AssignRoleRequest;
import com.procureiq.springboot_app.features.identity.dto.response.AuditEventResponse;
import com.procureiq.springboot_app.features.identity.dto.response.ChainVerificationResult;
import com.procureiq.springboot_app.features.identity.entity.relationships.AuditEvent;
import com.procureiq.springboot_app.features.identity.entity.relationships.RoleAssignment;
import com.procureiq.springboot_app.features.identity.service.AuditLogService;
import com.procureiq.springboot_app.features.identity.service.RoleManagementService;
import com.procureiq.springboot_app.shared.types.single.ApiSingleResponse;
import com.procureiq.springboot_app.shared.types.list.ApiListResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/identity")
@CrossOrigin(origins = "*")
public class IdentityAdminController {

    private final RoleManagementService roleManagementService;
    private final AuditLogService auditLogService;

    public IdentityAdminController(RoleManagementService roleManagementService, AuditLogService auditLogService) {
        this.roleManagementService = roleManagementService;
        this.auditLogService = auditLogService;
    }

    @PostMapping("/organizations/{orgId}/assignments")
    public ResponseEntity<?> assignRole(
            @PathVariable("orgId") Long orgId,
            @RequestParam("executorId") Long executorId,
            @Valid @RequestBody AssignRoleRequest request) {
        roleManagementService.assignRole(orgId, executorId, request);
        return ResponseEntity.ok(ApiSingleResponse.success(200, "Role assigned successfully"));
    }

    @GetMapping("/organizations/{orgId}/assignments")
    public ResponseEntity<?> getAssignments(
            @PathVariable("orgId") Long orgId,
            @RequestParam(value = "principalType", required = false, defaultValue = "user") String principalType,
            @RequestParam(value = "principalId", required = false, defaultValue = "1") Long principalId) {
        List<RoleAssignment> assignments = roleManagementService.getAssignments(orgId, principalType, principalId);
        List<com.procureiq.springboot_app.features.identity.dto.response.RoleAssignmentResponse> responseList = new java.util.ArrayList<>(assignments.size());
        for (RoleAssignment a : assignments) {
            responseList.add(new com.procureiq.springboot_app.features.identity.dto.response.RoleAssignmentResponse(
                a.getId(),
                a.getOrganization() != null ? a.getOrganization().getId() : orgId,
                a.getRole() != null ? a.getRole().getId() : null,
                a.getRole() != null ? a.getRole().getName() : "user",
                a.getPrincipalType(),
                a.getPrincipalId(),
                a.getScopeType(),
                a.getScopeId(),
                a.getExpiresAt(),
                a.getCreatedAt()
            ));
        }
        return ResponseEntity.ok(ApiListResponse.success(200, responseList));
    }

    @GetMapping("/organizations/{orgId}/audit-events")
    public ResponseEntity<?> getAuditEvents(@PathVariable("orgId") Long orgId) {
        List<AuditEvent> events = auditLogService.getLogs(orgId);
        List<AuditEventResponse> list = new java.util.ArrayList<>(events.size());
        for (AuditEvent e : events) {
            list.add(new AuditEventResponse(
                e.getId(),
                e.getOrganization() != null ? e.getOrganization().getId() : null,
                e.getActorType(),
                e.getActorId(),
                e.getAction(),
                e.getResourceType(),
                e.getResourceId(),
                e.getSeverity(),
                e.getBeforeValue(),
                e.getAfterValue(),
                e.getRequestId(),
                e.getSessionId(),
                e.getIpAddress(),
                e.getUserAgent(),
                e.getPrevHash(),
                e.getEntryHash(),
                e.getOccurredAt()
            ));
        }
        return ResponseEntity.ok(ApiListResponse.success(200, list));
    }

    @PostMapping("/organizations/{orgId}/audit-events/verify")
    public ResponseEntity<?> verifyAuditEvents(@PathVariable("orgId") Long orgId) {
        ChainVerificationResult result = auditLogService.verifyChainIntegrity(orgId);
        return ResponseEntity.ok(ApiSingleResponse.success(200, result));
    }
}
