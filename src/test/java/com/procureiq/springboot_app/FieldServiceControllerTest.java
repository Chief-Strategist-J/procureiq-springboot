package com.procureiq.springboot_app;

import tools.jackson.databind.json.JsonMapper;
import com.procureiq.springboot_app.features.fieldservice.dto.request.*;
import com.procureiq.springboot_app.features.fieldservice.dto.response.*;
import com.procureiq.springboot_app.features.fieldservice.entity.*;
import com.procureiq.springboot_app.features.fieldservice.repository.*;
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
public class FieldServiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OperatingHoursRepository operatingHoursRepository;

    @Autowired
    private ServiceTerritoryRepository serviceTerritoryRepository;

    @Autowired
    private ServiceResourceRepository serviceResourceRepository;

    @Autowired
    private WorkTypeRepository workTypeRepository;

    @Autowired
    private WorkOrderRepository workOrderRepository;

    @Autowired
    private ServiceAppointmentRepository serviceAppointmentRepository;

    @Autowired
    private AssignedResourceRepository assignedResourceRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    private final JsonMapper objectMapper = new JsonMapper();

    @BeforeEach
    public void setup() {
        assignedResourceRepository.deleteAll();
        serviceAppointmentRepository.deleteAll();
        workOrderRepository.deleteAll();
        workTypeRepository.deleteAll();
        serviceResourceRepository.deleteAll();
        serviceTerritoryRepository.deleteAll();
        operatingHoursRepository.deleteAll();
        accountRepository.deleteAll();
        appUserRepository.deleteAll();
    }

    @Test
    public void testFieldServiceFlow() throws Exception {
        Account account = new Account();
        account.setName("Test Account");
        account = accountRepository.save(account);
        Long accountId = account.getId();

        OperatingHoursRequest ohRequest = new OperatingHoursRequest("HQ Hours", "America/New_York");
        String ohResponseJson = mockMvc.perform(post("/api/v1/fieldservice/operating-hours")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ohRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.data.name", is("HQ Hours")))
                .andExpect(jsonPath("$.data.timezone", is("America/New_York")))
                .andReturn().getResponse().getContentAsString();

        Long ohId = operatingHoursRepository.findAll().get(0).getId();

        ServiceTerritoryRequest stRequest = new ServiceTerritoryRequest("East Coast", null, ohId, true);
        mockMvc.perform(post("/api/v1/fieldservice/territories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(stRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.data.name", is("East Coast")));

        ServiceResourceRequest srRequest = new ServiceResourceRequest("John Doe Crew", null, null, "crew", true);
        mockMvc.perform(post("/api/v1/fieldservice/resources")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(srRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.data.name", is("John Doe Crew")))
                .andExpect(jsonPath("$.data.resourceType", is("crew")));

        Long resourceId = serviceResourceRepository.findAll().get(0).getId();

        WorkTypeRequest wtRequest = new WorkTypeRequest("Installation", 120, 15);
        mockMvc.perform(post("/api/v1/fieldservice/work-types")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(wtRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.data.name", is("Installation")))
                .andExpect(jsonPath("$.data.defaultDurationMinutes", is(120)));

        Long workTypeId = workTypeRepository.findAll().get(0).getId();

        WorkOrderRequest woRequest = new WorkOrderRequest(null, null, accountId, null, null, null, workTypeId, null, "new", 3);
        mockMvc.perform(post("/api/v1/fieldservice/work-orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(woRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.data.status", is("new")));

        assertEquals(1, workOrderRepository.count());
        assertEquals(1, serviceAppointmentRepository.count());

        ServiceAppointment sa = serviceAppointmentRepository.findAll().get(0);
        Long appointmentId = sa.getId();
        Instant appointmentCreatedAt = sa.getCreatedAt();

        AssignResourceRequest assignRequest = new AssignResourceRequest(appointmentId, appointmentCreatedAt, resourceId, null, true, "assigned");
        mockMvc.perform(post("/api/v1/fieldservice/appointments/" + appointmentId + "/assign")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(assignRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.data.serviceAppointmentId", is(appointmentId)))
                .andExpect(jsonPath("$.data.serviceResourceId", is(resourceId)));

        assertEquals(1, assignedResourceRepository.count());
    }
}
