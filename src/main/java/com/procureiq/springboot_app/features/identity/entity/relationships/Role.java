package com.procureiq.springboot_app.features.identity.entity.relationships;

import com.procureiq.springboot_app.features.identity.entity.base.BaseRole;
import jakarta.persistence.*;

@Entity
@Table(name = "roles")
public class Role extends BaseRole {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "org_id")
    private com.procureiq.springboot_app.features.campaigns.entity.Organization organization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_role_id")
    private Role parentRole;

    public Role() {}

    public com.procureiq.springboot_app.features.campaigns.entity.Organization getOrganization() { return organization; }
    public void setOrganization(com.procureiq.springboot_app.features.campaigns.entity.Organization organization) { this.organization = organization; }
    public Role getParentRole() { return parentRole; }
    public void setParentRole(Role parentRole) { this.parentRole = parentRole; }
}
