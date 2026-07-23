package com.procureiq.springboot_app.features.identity.service;

import com.procureiq.springboot_app.features.identity.entity.relationships.RoleAssignment;
import com.procureiq.springboot_app.features.identity.entity.relationships.Role;
import com.procureiq.springboot_app.features.identity.entity.User;
import com.procureiq.springboot_app.features.campaigns.entity.Organization;
import com.procureiq.springboot_app.features.identity.repository.RoleAssignmentRepository;
import com.procureiq.springboot_app.features.identity.repository.RoleRepository;
import com.procureiq.springboot_app.features.identity.repository.IdentityUserRepository;
import com.procureiq.springboot_app.features.identity.dto.request.AssignRoleRequest;
import com.procureiq.springboot_app.shared.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class RoleManagementService {

    private final RoleAssignmentRepository roleAssignmentRepository;
    private final RoleRepository roleRepository;
    private final IdentityUserRepository userRepository;
    private final com.procureiq.springboot_app.features.campaigns.repository.OrganizationRepository organizationRepository;
    private final AuditLogService auditLogService;

    public RoleManagementService(
            RoleAssignmentRepository roleAssignmentRepository,
            RoleRepository roleRepository,
            IdentityUserRepository userRepository,
            com.procureiq.springboot_app.features.campaigns.repository.OrganizationRepository organizationRepository,
            AuditLogService auditLogService) {
        this.roleAssignmentRepository = roleAssignmentRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.organizationRepository = organizationRepository;
        this.auditLogService = auditLogService;
    }

    public List<RoleAssignment> getAssignments(Long orgId, String principalType, Long principalId) {
        return roleAssignmentRepository.findByOrganizationIdAndPrincipalTypeAndPrincipalId(orgId, principalType, principalId);
    }

    @Transactional
    public void assignRole(Long orgId, Long executorUserId, AssignRoleRequest request) {
        Organization organization = organizationRepository.findById(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found"));

        Role role = roleRepository.findById(request.roleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        User executor = userRepository.findById(executorUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Executor user not found"));

        RoleAssignment assignment = new RoleAssignment();
        assignment.setId(ThreadLocalRandom.current().nextLong(1, Long.MAX_VALUE));
        assignment.setOrganization(organization);
        assignment.setRole(role);
        assignment.setPrincipalType(request.principalType());
        assignment.setPrincipalId(request.principalId());
        assignment.setScopeType(request.scopeType());
        assignment.setScopeId(request.scopeId());
        assignment.setGranter(executor);

        if (request.expiresAfterSeconds() != null) {
            assignment.setExpiresAt(Instant.now().plusSeconds(request.expiresAfterSeconds()));
        }

        roleAssignmentRepository.save(assignment);

        String changesJson = String.format("{\"role_id\": %d, \"principal_type\": \"%s\", \"principal_id\": %d}", role.getId(), request.principalType(), request.principalId());
        auditLogService.log(
            orgId, "user", executorUserId, "role.granted", "role_assignment", assignment.getId(),
            "security_critical", null, changesJson, null, null, null, null
        );
    }
}
