package com.procureiq.springboot_app;

import tools.jackson.databind.json.JsonMapper;
import com.procureiq.springboot_app.features.campaigns.dto.*;
import com.procureiq.springboot_app.features.campaigns.entity.*;
import com.procureiq.springboot_app.features.campaigns.repository.*;
import com.procureiq.springboot_app.features.fieldservice.entity.Account;
import com.procureiq.springboot_app.features.fieldservice.entity.Contact;
import com.procureiq.springboot_app.features.fieldservice.repository.AccountRepository;
import com.procureiq.springboot_app.features.fieldservice.repository.ContactRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class CampaignControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CampaignRepository campaignRepository;

    @Autowired
    private ScheduledEmailRepository scheduledEmailRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    private final JsonMapper objectMapper = new JsonMapper();

    @BeforeEach
    public void setup() {
        scheduledEmailRepository.deleteAll();
        campaignRepository.deleteAll();
        contactRepository.deleteAll();
        accountRepository.deleteAll();
        organizationRepository.deleteAll();
    }

    @Test
    public void testCampaignCRUD() throws Exception {
        // 1. Create campaign
        CampaignRequest createReq = new CampaignRequest(1001L, "Summer Promo", "draft");

        String responseJson = mockMvc.perform(post("/api/v1/campaigns")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.data.name", is("Summer Promo")))
                .andExpect(jsonPath("$.data.status", is("draft")))
                .andReturn().getResponse().getContentAsString();

        CampaignResponse cResponse = objectMapper.readValue(
            objectMapper.readTree(responseJson).get("data").toString(),
            CampaignResponse.class
        );
        assertNotNull(cResponse.id());

        // 2. Get campaign
        mockMvc.perform(get("/api/v1/campaigns/" + cResponse.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name", is("Summer Promo")));

        // 3. Update campaign
        CampaignRequest updateReq = new CampaignRequest(1001L, "Summer Promo v2", "active");
        mockMvc.perform(put("/api/v1/campaigns/" + cResponse.id())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name", is("Summer Promo v2")))
                .andExpect(jsonPath("$.data.status", is("active")));

        // 4. Delete campaign
        mockMvc.perform(delete("/api/v1/campaigns/" + cResponse.id()))
                .andExpect(status().isOk());

        // 5. Get should fail/empty
        mockMvc.perform(get("/api/v1/campaigns/" + cResponse.id()))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testRecipientAndScheduleCRUD() throws Exception {
        // 1. Create Recipient (Contact)
        RecipientRequest recReq = new RecipientRequest(2001L, "John Doe", "john@example.com", "1234567890");

        String recJson = mockMvc.perform(post("/api/v1/campaigns/recipients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(recReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.data.name", is("John Doe")))
                .andReturn().getResponse().getContentAsString();

        RecipientResponse recResponse = objectMapper.readValue(
            objectMapper.readTree(recJson).get("data").toString(),
            RecipientResponse.class
        );

        // 2. Create Campaign
        Organization org = new Organization();
        org.setId(1002L);
        org.setName("Org 1002");
        org = organizationRepository.save(org);

        CampaignRequest campReq = new CampaignRequest(1002L, "Product Launch", "draft");
        Campaign c = new Campaign();
        c.setName("Product Launch");
        c.setOrganization(org);
        c = campaignRepository.save(c);

        // 3. Create Schedule
        ScheduledEmailRequest schedReq = new ScheduledEmailRequest(
            1002L,
            c.getId(),
            recResponse.id(),
            3001L,
            Instant.now().plusSeconds(3600),
            "pending"
        );

        String schedJson = mockMvc.perform(post("/api/v1/campaigns/schedules")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(schedReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.data.status", is("pending")))
                .andReturn().getResponse().getContentAsString();

        ScheduledEmailResponse schedResponse = objectMapper.readValue(
            objectMapper.readTree(schedJson).get("data").toString(),
            ScheduledEmailResponse.class
        );

        // 4. Update Schedule
        ScheduledEmailRequest updateSchedReq = new ScheduledEmailRequest(
            1002L,
            c.getId(),
            recResponse.id(),
            3001L,
            Instant.now().plusSeconds(7200),
            "sent"
        );

        mockMvc.perform(put("/api/v1/campaigns/schedules/" + schedResponse.id())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateSchedReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status", is("sent")));

        // 5. Delete Schedule
        mockMvc.perform(delete("/api/v1/campaigns/schedules/" + schedResponse.id()))
                .andExpect(status().isOk());
    }
}
