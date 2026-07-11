package com.procureiq.springboot_app;

import tools.jackson.databind.json.JsonMapper;
import com.procureiq.springboot_app.api.rest.v1.handlers.GitHubApiController.DispatchRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class GitHubApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final JsonMapper objectMapper = new JsonMapper();

    @Test
    public void testGitHubRepoInfoMock() throws Exception {
        mockMvc.perform(get("/api/v1/github/repo-info")
                .param("owner", "owner")
                .param("repo", "repo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.data.fullName", is("owner/repo")))
                .andExpect(jsonPath("$.data.name", is("repo")));
    }

    @Test
    public void testGitHubDispatchMock() throws Exception {
        DispatchRequest dispatchReq = new DispatchRequest("owner", "repo", "test_event", new HashMap<>());

        mockMvc.perform(post("/api/v1/github/dispatch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dispatchReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.data", is("Repository dispatch triggered successfully")));
    }

    @Test
    public void testGitHubWorkflowRunsMock() throws Exception {
        mockMvc.perform(get("/api/v1/github/workflow-runs")
                .param("owner", "owner")
                .param("repo", "repo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.data[0].id", notNullValue()));
    }

    @Test
    public void testGitHubDispatchValidation() throws Exception {
        DispatchRequest invalidReq = new DispatchRequest("", "repo", "test_event", new HashMap<>());

        mockMvc.perform(post("/api/v1/github/dispatch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidReq)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is("error")))
                .andExpect(jsonPath("$.error.message", containsString("Repository owner is required")));
    }
}
