package com.procureiq.springboot_app;

import tools.jackson.databind.json.JsonMapper;
import com.procureiq.springboot_app.features.jobs.dto.*;
import com.procureiq.springboot_app.features.jobs.repository.*;
import com.procureiq.springboot_app.features.campaigns.repository.OrganizationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class JobControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private JobRunRepository jobRunRepository;

    @Autowired
    private WorkflowRepository workflowRepository;

    @Autowired
    private WorkflowRunRepository workflowRunRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    private final JsonMapper objectMapper = new JsonMapper();

    @BeforeEach
    public void setup() {
        jobRunRepository.deleteAll();
        jobRepository.deleteAll();
        workflowRunRepository.deleteAll();
        workflowRepository.deleteAll();
        organizationRepository.deleteAll();
    }

    @Test
    public void testJobCRUDAndTriggerRun() throws Exception {
        // 1. Create Job
        Map<String, Object> config = new HashMap<>();
        config.put("command", "echo 'hello'");
        JobRequest createReq = new JobRequest(2001L, null, "Test ETL Job", "active", config);

        String responseJson = mockMvc.perform(post("/api/v1/jobs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.data.name", is("Test ETL Job")))
                .andExpect(jsonPath("$.data.status", is("active")))
                .andReturn().getResponse().getContentAsString();

        JobResponse jobResponse = objectMapper.readValue(
                objectMapper.readTree(responseJson).get("data").toString(),
                JobResponse.class
        );
        assertNotNull(jobResponse.id());

        // 2. Get Job
        mockMvc.perform(get("/api/v1/jobs/" + jobResponse.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name", is("Test ETL Job")));

        // 3. Update Job
        JobRequest updateReq = new JobRequest(2001L, null, "Test ETL Job Updated", "inactive", config);
        mockMvc.perform(put("/api/v1/jobs/" + jobResponse.id())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name", is("Test ETL Job Updated")))
                .andExpect(jsonPath("$.data.status", is("inactive")));

        // 4. Trigger Job Run
        String runResponseJson = mockMvc.perform(post("/api/v1/jobs/" + jobResponse.id() + "/runs"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.data.status", is("running")))
                .andReturn().getResponse().getContentAsString();

        JobRunResponse runResponse = objectMapper.readValue(
                objectMapper.readTree(runResponseJson).get("data").toString(),
                JobRunResponse.class
        );
        assertNotNull(runResponse.id());

        // 5. Get Job Run
        mockMvc.perform(get("/api/v1/jobs/runs/" + runResponse.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status", is("running")));

        // 6. Get Job Runs List
        mockMvc.perform(get("/api/v1/jobs/" + jobResponse.id() + "/runs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)));

        // 7. Delete Job
        mockMvc.perform(delete("/api/v1/jobs/" + jobResponse.id()))
                .andExpect(status().isOk());
    }

    @Test
    public void testWorkflowCRUDAndTriggerRun() throws Exception {
        // 1. Create Workflow
        WorkflowRequest createReq = new WorkflowRequest(3001L, "Data Sync Flow", "draft");

        String responseJson = mockMvc.perform(post("/api/v1/workflows")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.data.name", is("Data Sync Flow")))
                .andExpect(jsonPath("$.data.status", is("draft")))
                .andReturn().getResponse().getContentAsString();

        WorkflowResponse workflowResponse = objectMapper.readValue(
                objectMapper.readTree(responseJson).get("data").toString(),
                WorkflowResponse.class
        );
        assertNotNull(workflowResponse.id());

        // 2. Get Workflow
        mockMvc.perform(get("/api/v1/workflows/" + workflowResponse.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name", is("Data Sync Flow")));

        // 3. Update Workflow
        WorkflowRequest updateReq = new WorkflowRequest(3001L, "Data Sync Flow Updated", "active");
        mockMvc.perform(put("/api/v1/workflows/" + workflowResponse.id())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name", is("Data Sync Flow Updated")))
                .andExpect(jsonPath("$.data.status", is("active")));

        // 4. Trigger Workflow Run
        String runResponseJson = mockMvc.perform(post("/api/v1/workflows/" + workflowResponse.id() + "/runs"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.data.status", is("running")))
                .andReturn().getResponse().getContentAsString();

        WorkflowRunResponse runResponse = objectMapper.readValue(
                objectMapper.readTree(runResponseJson).get("data").toString(),
                WorkflowRunResponse.class
        );
        assertNotNull(runResponse.id());

        // 5. Get Workflow Run
        mockMvc.perform(get("/api/v1/workflows/runs/" + runResponse.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status", is("running")));

        // 6. Get Workflow Runs List
        mockMvc.perform(get("/api/v1/workflows/" + workflowResponse.id() + "/runs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)));

        // 7. Delete Workflow
        mockMvc.perform(delete("/api/v1/workflows/" + workflowResponse.id()))
                .andExpect(status().isOk());
    }
}
