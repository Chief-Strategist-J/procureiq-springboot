package com.procureiq.springboot_app;

import tools.jackson.databind.json.JsonMapper;
import com.procureiq.springboot_app.api.rest.v1.handlers.GmailApiController.SendEmailRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class GmailApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final JsonMapper objectMapper = new JsonMapper();

    @Test
    public void testGmailSendAndListMock() throws Exception {
        // 1. Send an email
        SendEmailRequest sendReq = new SendEmailRequest("recipient@example.com", "Test Subject", "Test Body");

        mockMvc.perform(post("/api/v1/gmail/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sendReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.data.id", notNullValue()));

        // 2. List emails
        mockMvc.perform(get("/api/v1/gmail/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    public void testGmailSendValidation() throws Exception {
        // Invalid request (missing recipient)
        SendEmailRequest invalidReq = new SendEmailRequest("", "Test Subject", "Test Body");

        mockMvc.perform(post("/api/v1/gmail/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidReq)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is("error")))
                .andExpect(jsonPath("$.error.message", containsString("Recipient 'to' address is required")));
    }
}
