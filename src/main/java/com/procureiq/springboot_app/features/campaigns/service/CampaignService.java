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

    // --- Organization Helper ---
    private Organization getOrCreateOrganization(Long orgId) {
        return organizationRepository.findById(orgId).orElseGet(() -> {
            Organization org = new Organization();
            org.setId(orgId);
            org.setName("Org " + orgId);
            return organizationRepository.save(org);
        });
    }

    // --- Campaign CRUD ---

    @Transactional(readOnly = true)
    public List<CampaignResponse> getAllCampaigns() {
        Span span = tracer.spanBuilder("CampaignService.getAllCampaigns").startSpan();
        try {
            return campaignRepository.findAll().stream()
                    .map(c -> new CampaignResponse(
                            c.getId(),
                            c.getOrganization().getId(),
                            c.getName(),
                            c.getStatus(),
                            c.getCreatedAt(),
                            c.getUpdatedAt()))
                    .collect(Collectors.toList());
        } finally {
            span.end();
        }
    }

    @Transactional
    public CampaignResponse createCampaign(CampaignRequest request) {
        Span span = tracer.spanBuilder("CampaignService.createCampaign").startSpan();
        try {
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
        } finally {
            span.end();
        }
    }

    @Transactional(readOnly = true)
    public CampaignResponse getCampaign(Long id) {
        Span span = tracer.spanBuilder("CampaignService.getCampaign").startSpan();
        try {
            Campaign c = campaignRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Campaign not found: " + id));
            return new CampaignResponse(
                    c.getId(),
                    c.getOrganization().getId(),
                    c.getName(),
                    c.getStatus(),
                    c.getCreatedAt(),
                    c.getUpdatedAt());
        } finally {
            span.end();
        }
    }

    @Transactional
    public CampaignResponse updateCampaign(Long id, CampaignRequest request) {
        Span span = tracer.spanBuilder("CampaignService.updateCampaign").startSpan();
        try {
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
        } finally {
            span.end();
        }
    }

    @Transactional
    public void deleteCampaign(Long id) {
        Span span = tracer.spanBuilder("CampaignService.deleteCampaign").startSpan();
        try {
            campaignRepository.deleteById(id);
        } finally {
            span.end();
        }
    }

    // --- Scheduled Email CRUD ---

    @Transactional(readOnly = true)
    public List<ScheduledEmailResponse> getAllScheduledEmails() {
        Span span = tracer.spanBuilder("CampaignService.getAllScheduledEmails").startSpan();
        try {
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
        } finally {
            span.end();
        }
    }

    @Transactional
    public ScheduledEmailResponse createScheduledEmail(ScheduledEmailRequest request) {
        Span span = tracer.spanBuilder("CampaignService.createScheduledEmail").startSpan();
        try {
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
        } finally {
            span.end();
        }
    }

    @Transactional(readOnly = true)
    public ScheduledEmailResponse getScheduledEmail(Long id) {
        Span span = tracer.spanBuilder("CampaignService.getScheduledEmail").startSpan();
        try {
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
        } finally {
            span.end();
        }
    }

    @Transactional
    public ScheduledEmailResponse updateScheduledEmail(Long id, ScheduledEmailRequest request) {
        Span span = tracer.spanBuilder("CampaignService.updateScheduledEmail").startSpan();
        try {
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
        } finally {
            span.end();
        }
    }

    @Transactional
    public void deleteScheduledEmail(Long id) {
        Span span = tracer.spanBuilder("CampaignService.deleteScheduledEmail").startSpan();
        try {
            scheduledEmailRepository.deleteById(id);
        } finally {
            span.end();
        }
    }

    // --- Recipient CRUD ---

    @Transactional(readOnly = true)
    public List<RecipientResponse> getAllRecipients() {
        Span span = tracer.spanBuilder("CampaignService.getAllRecipients").startSpan();
        try {
            return contactRepository.findAll().stream()
                    .map(c -> new RecipientResponse(
                            c.getId(),
                            c.getAccount().getId(),
                            c.getName(),
                            c.getEmail(),
                            c.getPhone()))
                    .collect(Collectors.toList());
        } finally {
            span.end();
        }
    }

    @Transactional
    public RecipientResponse createRecipient(RecipientRequest request) {
        Span span = tracer.spanBuilder("CampaignService.createRecipient").startSpan();
        try {
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
        } finally {
            span.end();
        }
    }

    @Transactional(readOnly = true)
    public RecipientResponse getRecipient(Long id) {
        Span span = tracer.spanBuilder("CampaignService.getRecipient").startSpan();
        try {
            Contact contact = contactRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Recipient not found: " + id));
            return new RecipientResponse(
                    contact.getId(),
                    contact.getAccount().getId(),
                    contact.getName(),
                    contact.getEmail(),
                    contact.getPhone());
        } finally {
            span.end();
        }
    }

    @Transactional
    public RecipientResponse updateRecipient(Long id, RecipientRequest request) {
        Span span = tracer.spanBuilder("CampaignService.updateRecipient").startSpan();
        try {
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
        } finally {
            span.end();
        }
    }

    @Transactional
    public void deleteRecipient(Long id) {
        Span span = tracer.spanBuilder("CampaignService.deleteRecipient").startSpan();
        try {
            contactRepository.deleteById(id);
        } finally {
            span.end();
        }
    }
}
