package com.procureiq.springboot_app.features.campaigns.service;

import com.procureiq.springboot_app.features.campaigns.dto.request.*;
import com.procureiq.springboot_app.features.campaigns.dto.response.*;
import com.procureiq.springboot_app.features.campaigns.entity.Campaign;
import com.procureiq.springboot_app.features.campaigns.entity.Organization;
import com.procureiq.springboot_app.features.campaigns.entity.ScheduledEmail;
import com.procureiq.springboot_app.features.campaigns.repository.CampaignRepository;
import com.procureiq.springboot_app.features.campaigns.repository.OrganizationRepository;
import com.procureiq.springboot_app.features.campaigns.repository.ScheduledEmailRepository;
import com.procureiq.springboot_app.features.fieldservice.entity.Account;
import com.procureiq.springboot_app.features.fieldservice.entity.Contact;
import com.procureiq.springboot_app.features.fieldservice.repository.AccountRepository;
import com.procureiq.springboot_app.features.fieldservice.repository.ContactRepository;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CampaignService {

    private final CampaignRepository campaignRepository;
    private final ScheduledEmailRepository scheduledEmailRepository;
    private final ContactRepository contactRepository;
    private final OrganizationRepository organizationRepository;
    private final AccountRepository accountRepository;
    private final Tracer tracer = GlobalOpenTelemetry.getTracer("springboot-app", "1.0.0");

    public CampaignService(
            CampaignRepository campaignRepository,
            ScheduledEmailRepository scheduledEmailRepository,
            ContactRepository contactRepository,
            OrganizationRepository organizationRepository,
            AccountRepository accountRepository) {
        this.campaignRepository = campaignRepository;
        this.scheduledEmailRepository = scheduledEmailRepository;
        this.contactRepository = contactRepository;
        this.organizationRepository = organizationRepository;
        this.accountRepository = accountRepository;
    }

    
    private Organization getOrCreateOrganization(Long orgId) {
        return organizationRepository.findById(orgId).orElseGet(() -> {
            Organization org = new Organization();
            org.setId(orgId);
            org.setName("Org " + orgId);
            return organizationRepository.save(org);
        });
    }

    

    @Transactional(readOnly = true)
    public List<CampaignResponse> getAllCampaigns() {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeServiceWithTracing(() -> {
            return campaignRepository.findAll().stream()
                    .map(c -> new CampaignResponse(
                            c.getId(),
                            c.getOrganization().getId(),
                            c.getName(),
                            c.getStatus(),
                            c.getCreatedAt(),
                            c.getUpdatedAt()))
                    .collect(Collectors.toList());
        });
    }

    @Transactional
    public CampaignResponse createCampaign(CampaignRequest request) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeServiceWithTracing(() -> {
            Organization org = getOrCreateOrganization(request.orgId());
            Campaign c = new Campaign();
            c.setOrganization(org);
            c.setName(request.name());
            c.setStatus(request.status() != null ? request.status() : "draft");
            c = campaignRepository.save(c);
            return new CampaignResponse(
                    c.getId(),
                    c.getOrganization().getId(),
                    c.getName(),
                    c.getStatus(),
                    c.getCreatedAt(),
                    c.getUpdatedAt());
        });
    }

    @Transactional(readOnly = true)
    public CampaignResponse getCampaign(Long id) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeServiceWithTracing(() -> {
            Campaign c = campaignRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Campaign not found: " + id));
            return new CampaignResponse(
                    c.getId(),
                    c.getOrganization().getId(),
                    c.getName(),
                    c.getStatus(),
                    c.getCreatedAt(),
                    c.getUpdatedAt());
        });
    }

    @Transactional
    public CampaignResponse updateCampaign(Long id, CampaignRequest request) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeServiceWithTracing(() -> {
            Campaign c = campaignRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Campaign not found: " + id));
            if (request.orgId() != null) {
                c.setOrganization(getOrCreateOrganization(request.orgId()));
            }
            c.setName(request.name());
            if (request.status() != null) {
                c.setStatus(request.status());
            }
            c.setUpdatedAt(Instant.now());
            c = campaignRepository.save(c);
            return new CampaignResponse(
                    c.getId(),
                    c.getOrganization().getId(),
                    c.getName(),
                    c.getStatus(),
                    c.getCreatedAt(),
                    c.getUpdatedAt());
        });
    }

    @Transactional
    public void deleteCampaign(Long id) {
        com.procureiq.springboot_app.infra.config.TracingHelper.executeServiceVoidWithTracing(() -> {
            campaignRepository.deleteById(id);
        });
    }

    

    @Transactional(readOnly = true)
    public List<ScheduledEmailResponse> getAllScheduledEmails() {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeServiceWithTracing(() -> {
            return scheduledEmailRepository.findAll().stream()
                    .map(se -> new ScheduledEmailResponse(
                            se.getId(),
                            se.getOrganization().getId(),
                            se.getCampaign() != null ? se.getCampaign().getId() : null,
                            se.getContact().getId(),
                            se.getTemplateId(),
                            se.getScheduledAt(),
                            se.getStatus(),
                            se.getCreatedAt(),
                            se.getUpdatedAt()))
                    .collect(Collectors.toList());
        });
    }

    @Transactional
    public ScheduledEmailResponse createScheduledEmail(ScheduledEmailRequest request) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeServiceWithTracing(() -> {
            Organization org = getOrCreateOrganization(request.orgId());
            Contact contact = contactRepository.findById(request.contactId())
                    .orElseThrow(() -> new IllegalArgumentException("Contact not found: " + request.contactId()));
            Campaign campaign = null;
            if (request.campaignId() != null) {
                campaign = campaignRepository.findById(request.campaignId())
                        .orElseThrow(() -> new IllegalArgumentException("Campaign not found: " + request.campaignId()));
            }

            ScheduledEmail se = new ScheduledEmail();
            se.setOrganization(org);
            se.setCampaign(campaign);
            se.setContact(contact);
            se.setTemplateId(request.templateId());
            se.setScheduledAt(request.scheduledAt() != null ? request.scheduledAt() : Instant.now());
            se.setStatus(request.status() != null ? request.status() : "pending");
            se = scheduledEmailRepository.save(se);

            return new ScheduledEmailResponse(
                    se.getId(),
                    se.getOrganization().getId(),
                    se.getCampaign() != null ? se.getCampaign().getId() : null,
                    se.getContact().getId(),
                    se.getTemplateId(),
                    se.getScheduledAt(),
                    se.getStatus(),
                    se.getCreatedAt(),
                    se.getUpdatedAt());
        });
    }

    @Transactional(readOnly = true)
    public ScheduledEmailResponse getScheduledEmail(Long id) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeServiceWithTracing(() -> {
            ScheduledEmail se = scheduledEmailRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Scheduled email not found: " + id));
            return new ScheduledEmailResponse(
                    se.getId(),
                    se.getOrganization().getId(),
                    se.getCampaign() != null ? se.getCampaign().getId() : null,
                    se.getContact().getId(),
                    se.getTemplateId(),
                    se.getScheduledAt(),
                    se.getStatus(),
                    se.getCreatedAt(),
                    se.getUpdatedAt());
        });
    }

    @Transactional
    public ScheduledEmailResponse updateScheduledEmail(Long id, ScheduledEmailRequest request) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeServiceWithTracing(() -> {
            ScheduledEmail se = scheduledEmailRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Scheduled email not found: " + id));
            if (request.orgId() != null) {
                se.setOrganization(getOrCreateOrganization(request.orgId()));
            }
            if (request.contactId() != null) {
                Contact contact = contactRepository.findById(request.contactId())
                        .orElseThrow(() -> new IllegalArgumentException("Contact not found: " + request.contactId()));
                se.setContact(contact);
            }
            if (request.campaignId() != null) {
                Campaign campaign = campaignRepository.findById(request.campaignId())
                        .orElseThrow(() -> new IllegalArgumentException("Campaign not found: " + request.campaignId()));
                se.setCampaign(campaign);
            } else {
                se.setCampaign(null);
            }
            se.setTemplateId(request.templateId());
            if (request.scheduledAt() != null) {
                se.setScheduledAt(request.scheduledAt());
            }
            if (request.status() != null) {
                se.setStatus(request.status());
            }
            se.setUpdatedAt(Instant.now());
            se = scheduledEmailRepository.save(se);

            return new ScheduledEmailResponse(
                    se.getId(),
                    se.getOrganization().getId(),
                    se.getCampaign() != null ? se.getCampaign().getId() : null,
                    se.getContact().getId(),
                    se.getTemplateId(),
                    se.getScheduledAt(),
                    se.getStatus(),
                    se.getCreatedAt(),
                    se.getUpdatedAt());
        });
    }

    @Transactional
    public void deleteScheduledEmail(Long id) {
        com.procureiq.springboot_app.infra.config.TracingHelper.executeServiceVoidWithTracing(() -> {
            scheduledEmailRepository.deleteById(id);
        });
    }

    

    @Transactional(readOnly = true)
    public List<RecipientResponse> getAllRecipients() {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeServiceWithTracing(() -> {
            return contactRepository.findAll().stream()
                    .map(c -> new RecipientResponse(
                            c.getId(),
                            c.getAccount().getId(),
                            c.getName(),
                            c.getEmail(),
                            c.getPhone()))
                    .collect(Collectors.toList());
        });
    }

    @Transactional
    public RecipientResponse createRecipient(RecipientRequest request) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeServiceWithTracing(() -> {
            Account account = accountRepository.findById(request.accountId()).orElseGet(() -> {
                Account acc = new Account();
                acc.setId(request.accountId());
                acc.setName("Account " + request.accountId());
                return accountRepository.save(acc);
            });

            Contact contact = new Contact();
            contact.setAccount(account);
            contact.setName(request.name());
            contact.setEmail(request.email());
            contact.setPhone(request.phone());
            contact = contactRepository.save(contact);

            return new RecipientResponse(
                    contact.getId(),
                    contact.getAccount().getId(),
                    contact.getName(),
                    contact.getEmail(),
                    contact.getPhone());
        });
    }

    @Transactional(readOnly = true)
    public RecipientResponse getRecipient(Long id) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeServiceWithTracing(() -> {
            Contact contact = contactRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Recipient not found: " + id));
            return new RecipientResponse(
                    contact.getId(),
                    contact.getAccount().getId(),
                    contact.getName(),
                    contact.getEmail(),
                    contact.getPhone());
        });
    }

    @Transactional
    public RecipientResponse updateRecipient(Long id, RecipientRequest request) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeServiceWithTracing(() -> {
            Contact contact = contactRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Recipient not found: " + id));
            if (request.accountId() != null) {
                Account account = accountRepository.findById(request.accountId()).orElseGet(() -> {
                    Account acc = new Account();
                    acc.setId(request.accountId());
                    acc.setName("Account " + request.accountId());
                    return accountRepository.save(acc);
                });
                contact.setAccount(account);
            }
            contact.setName(request.name());
            contact.setEmail(request.email());
            contact.setPhone(request.phone());
            contact = contactRepository.save(contact);

            return new RecipientResponse(
                    contact.getId(),
                    contact.getAccount().getId(),
                    contact.getName(),
                    contact.getEmail(),
                    contact.getPhone());
        });
    }

    @Transactional
    public void deleteRecipient(Long id) {
        com.procureiq.springboot_app.infra.config.TracingHelper.executeServiceVoidWithTracing(() -> {
            contactRepository.deleteById(id);
        });
    }
}
