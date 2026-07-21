package com.procureiq.springboot_app.features.identity.entity.relationships;

import com.procureiq.springboot_app.features.identity.entity.base.BaseServiceAccount;
import jakarta.persistence.*;

@Entity
@Table(name = "service_accounts")
public class ServiceAccount extends BaseServiceAccount {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "org_id", nullable = false)
    private com.procureiq.springboot_app.features.campaigns.entity.Organization organization;

    public ServiceAccount() {}

    public com.procureiq.springboot_app.features.campaigns.entity.Organization getOrganization() { return organization; }
    public void setOrganization(com.procureiq.springboot_app.features.campaigns.entity.Organization organization) { this.organization = organization; }
}
