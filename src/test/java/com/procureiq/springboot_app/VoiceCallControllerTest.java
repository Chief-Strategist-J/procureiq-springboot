package com.procureiq.springboot_app;

import tools.jackson.databind.json.JsonMapper;
import com.procureiq.springboot_app.features.voice.entity.ScheduledCall;
import com.procureiq.springboot_app.features.voice.repository.ScheduledCallRepository;
import com.procureiq.springboot_app.features.voice.worker.VoiceCallBackgroundWorker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class VoiceCallControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ScheduledCallRepository scheduledCallRepository;

    @Autowired
    private VoiceCallBackgroundWorker voiceCallBackgroundWorker;

    private final JsonMapper objectMapper = new JsonMapper();

    @BeforeEach
    public void setup() {
        scheduledCallRepository.deleteAll();
    }


    @Test
    public void testScheduleCallSuccess() throws Exception {
        Map<String, Object> body = Map.of(
            "phoneNumber",  "+15551234567",
            "instructions", "Hello, this is a test call.",
            "scheduledAt",  Instant.now().plusSeconds(300).toString(),
            "provider",     "mock"
        );

        mockMvc.perform(post("/api/v1/voice/schedule")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.data.id", notNullValue()))
            .andExpect(jsonPath("$.data.phoneNumber", is("+15551234567")))
            .andExpect(jsonPath("$.data.status", is("PENDING")))
            .andExpect(jsonPath("$.data.provider", is("mock")));
    }

    @Test
    public void testScheduleCallMissingPhoneNumber() throws Exception {
        Map<String, Object> body = Map.of(
            "instructions", "Missing phone number test.",
            "scheduledAt",  Instant.now().plusSeconds(60).toString()
        );

        mockMvc.perform(post("/api/v1/voice/schedule")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error.message", containsString("phoneNumber")));
    }

    @Test
    public void testScheduleCallMissingInstructions() throws Exception {
        Map<String, Object> body = Map.of(
            "phoneNumber", "+15551234567",
            "scheduledAt", Instant.now().plusSeconds(60).toString()
        );

        mockMvc.perform(post("/api/v1/voice/schedule")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error.message", containsString("instructions")));
    }

    @Test
    public void testScheduleCallMissingScheduledAt() throws Exception {
        Map<String, Object> body = Map.of(
            "phoneNumber",  "+15551234567",
            "instructions", "Missing scheduledAt test."
        );

        mockMvc.perform(post("/api/v1/voice/schedule")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error.message", containsString("scheduledAt")));
    }

    @Test
    public void testScheduleCallInvalidScheduledAt() throws Exception {
        Map<String, Object> body = Map.of(
            "phoneNumber",  "+15551234567",
            "instructions", "Test call.",
            "scheduledAt",  "not-a-timestamp"
        );

        mockMvc.perform(post("/api/v1/voice/schedule")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error.message", containsString("ISO-8601")));
    }


    @Test
    public void testListScheduledCallsEmpty() throws Exception {
        mockMvc.perform(get("/api/v1/voice/scheduled"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data", hasSize(0)));
    }

    @Test
    public void testListScheduledCallsAfterScheduling() throws Exception {
        for (int i = 1; i <= 2; i++) {
            Map<String, Object> body = Map.of(
                "phoneNumber",  "+1555000000" + i,
                "instructions", "Call number " + i,
                "scheduledAt",  Instant.now().plusSeconds(i * 60L).toString()
            );
            mockMvc.perform(post("/api/v1/voice/schedule")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated());
        }

        mockMvc.perform(get("/api/v1/voice/scheduled"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data", hasSize(2)));
    }


    @Test
    public void testDeleteScheduledCallSuccess() throws Exception {
        ScheduledCall call = new ScheduledCall();
        call.setPhoneNumber("+15559876543");
        call.setInstructions("Delete me.");
        call.setScheduledAt(Instant.now().plusSeconds(600));
        call.setStatus("PENDING");
        call.setProvider("mock");
        call.setCreatedAt(Instant.now());
        ScheduledCall saved = scheduledCallRepository.save(call);

        mockMvc.perform(delete("/api/v1/voice/" + saved.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data", containsString("deleted")));

        assertFalse(scheduledCallRepository.existsById(saved.getId()));
    }

    @Test
    public void testDeleteScheduledCallNotFound() throws Exception {
        mockMvc.perform(delete("/api/v1/voice/999999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error.message", containsString("not found")));
    }


    @Test
    public void testBackgroundWorkerProcessesDueCalls() {
        ScheduledCall call = new ScheduledCall();
        call.setPhoneNumber("+15550001111");
        call.setInstructions("This call is due now.");
        call.setScheduledAt(Instant.now().minusSeconds(30));
        call.setStatus("PENDING");
        call.setProvider("mock");
        call.setCreatedAt(Instant.now());
        scheduledCallRepository.save(call);

        voiceCallBackgroundWorker.processScheduledCalls();

        List<ScheduledCall> results = scheduledCallRepository.findAll();
        assertEquals(1, results.size());
        assertEquals("CALLED", results.get(0).getStatus());
    }

    @Test
    public void testBackgroundWorkerSkipsFutureCalls() {
        ScheduledCall call = new ScheduledCall();
        call.setPhoneNumber("+15550002222");
        call.setInstructions("This call is not due yet.");
        call.setScheduledAt(Instant.now().plusSeconds(3600));
        call.setStatus("PENDING");
        call.setProvider("mock");
        call.setCreatedAt(Instant.now());
        scheduledCallRepository.save(call);

        voiceCallBackgroundWorker.processScheduledCalls();

        List<ScheduledCall> results = scheduledCallRepository.findAll();
        assertEquals(1, results.size());
        assertEquals("PENDING", results.get(0).getStatus());
    }

    @Test
    public void testBackgroundWorkerSkipsAlreadyCalledRecords() {
        ScheduledCall call = new ScheduledCall();
        call.setPhoneNumber("+15550003333");
        call.setInstructions("Already called.");
        call.setScheduledAt(Instant.now().minusSeconds(60));
        call.setStatus("CALLED");
        call.setProvider("mock");
        call.setCreatedAt(Instant.now());
        scheduledCallRepository.save(call);

        voiceCallBackgroundWorker.processScheduledCalls();

        List<ScheduledCall> results = scheduledCallRepository.findAll();
        assertEquals(1, results.size());
        assertEquals("CALLED", results.get(0).getStatus());
    }
}
