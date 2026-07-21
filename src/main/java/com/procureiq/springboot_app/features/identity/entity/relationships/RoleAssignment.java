package com.procureiq.springboot_app.features.identity.entity.relationships;

import com.procureiq.springboot_app.features.identity.entity.base.BaseRoleAssignment;
import com.procureiq.springboot_app.features.identity.entity.User;
import jakarta.persistence.*;

@Entity
@Table(name = "role_assignments")
public class RoleAssignment extends BaseRoleAssignment {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "org_id", nullable = false)
    private com.procureiq.springboot_app.features.campaigns.entity.Organization organization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "granted_by", nullable = false)
    private User granter;

    public RoleAssignment() {}

    public com.procureiq.springboot_app.features.campaigns.entity.Organization getOrganization() { return organization; }
    public void setOrganization(com.procureiq.springboot_app.features.campaigns.entity.Organization organization) { this.organization = organization; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public User getGranter() { return granter; }
    public void setGranter(User granter) { this.granter = granter; }
}
