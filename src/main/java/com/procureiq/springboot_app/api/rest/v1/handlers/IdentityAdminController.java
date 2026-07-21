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
            @RequestParam("principalType") String principalType,
            @RequestParam("principalId") Long principalId) {
        List<RoleAssignment> list = roleManagementService.getAssignments(orgId, principalType, principalId);
        return ResponseEntity.ok(ApiListResponse.success(list));
    }

    @GetMapping("/organizations/{orgId}/audit-events")
    public ResponseEntity<?> getAuditEvents(@PathVariable("orgId") Long orgId) {
        List<AuditEvent> events = auditLogService.getLogs(orgId);
        List<AuditEventResponse> list = events.stream()
                .map(e -> new AuditEventResponse(
                        e.getId(), e.getOrgId(), e.getActorType(), e.getActorId(),
                        e.getAction(), e.getResourceType(), e.getResourceId(),
                        e.getSeverity(), e.getBeforeValue(), e.getAfterValue(),
                        e.getRequestId(), e.getSessionId(), e.getIpAddress(),
                        e.getUserAgent(), e.getPrevHash(), e.getEntryHash(),
                        e.getOccurredAt()
                )).collect(Collectors.toList());
        return ResponseEntity.ok(ApiListResponse.success(list));
    }

    @PostMapping("/organizations/{orgId}/audit-events/verify")
    public ResponseEntity<?> verifyAuditEvents(@PathVariable("orgId") Long orgId) {
        ChainVerificationResult result = auditLogService.verifyChainIntegrity(orgId);
        return ResponseEntity.ok(ApiSingleResponse.success(200, result));
    }
}
