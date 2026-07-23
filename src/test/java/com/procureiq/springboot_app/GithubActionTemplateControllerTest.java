package com.procureiq.springboot_app;

import com.procureiq.springboot_app.features.githubactions.entity.GithubActionTemplate;
import com.procureiq.springboot_app.features.githubactions.repository.GithubActionTemplateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class GithubActionTemplateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GithubActionTemplateRepository githubActionTemplateRepository;

    @BeforeEach
    public void setup() {
        githubActionTemplateRepository.deleteAll();
    }

    private GithubActionTemplate seedTemplate() {
        GithubActionTemplate t = new GithubActionTemplate();
        t.setName("Test Lint Sweep");
        t.setCategory("Code Quality & Review");
        t.setDescription("Run linters and static analysis across active branches");
        t.setCronExpression("15 7 * * *");
        t.setEventType("test_lint_sweep.requested");
        t.setYamlContent("name: \"[Daily] Test Lint Sweep\"\non:\n  workflow_dispatch:\n");
        return githubActionTemplateRepository.save(t);
    }

    @Test
    public void testListTemplates() throws Exception {
        seedTemplate();

        mockMvc.perform(get("/api/v1/github/templates"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].name", is("Test Lint Sweep")))
                .andExpect(jsonPath("$.data[0].eventType", is("test_lint_sweep.requested")));
    }

    @Test
    public void testGetTemplateById() throws Exception {
        GithubActionTemplate saved = seedTemplate();

        mockMvc.perform(get("/api/v1/github/templates/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.category", is("Code Quality & Review")))
                .andExpect(jsonPath("$.data.yamlContent", containsString("workflow_dispatch")));
    }

    @Test
    public void testGetTemplateByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/github/templates/999999"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is("error")));
    }
}
