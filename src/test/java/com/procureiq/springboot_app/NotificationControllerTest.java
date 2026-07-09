package com.procureiq.springboot_app;

import tools.jackson.databind.json.JsonMapper;
import com.procureiq.springboot_app.features.notifications.dto.*;
import com.procureiq.springboot_app.features.notifications.entity.NotificationType;
import com.procureiq.springboot_app.features.notifications.entity.ChannelDelivery;
import com.procureiq.springboot_app.features.notifications.repository.*;
import java.util.List;
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
public class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private NotificationTypeRepository typeRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationRecipientRepository recipientRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private ChannelDeliveryRepository channelDeliveryRepository;

    @Autowired
    private com.procureiq.springboot_app.features.notifications.worker.NotificationBackgroundWorker notificationBackgroundWorker;

    private final JsonMapper objectMapper = new JsonMapper();

    @BeforeEach
    public void setup() {
        recipientRepository.deleteAll();
        notificationRepository.deleteAll();
        deviceRepository.deleteAll();
        channelDeliveryRepository.deleteAll();
        typeRepository.deleteAll();
        typeRepository.flush();

        // Seed a notification type
        NotificationType type = new NotificationType();
        type.setCode("system_alert");
        type.setCategory("transactional");
        type.setDefaultPriority((short) 3);
        type.setDefaultChannels(new String[]{"push", "email"});
        type.setFanOutMode("write");
        typeRepository.save(type);
    }

    @Test
    public void testSendAndFetchNotification() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("message", "This is a test notification");

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("deep_link", "app://test");

        SendNotificationRequest request = new SendNotificationRequest(
            "system_alert",
            "test-service",
            "user",
            100L, // target user ID
            null,
            payload,
            metadata,
            3,
            null
        );

        // 1. Trigger the notification
        mockMvc.perform(post("/api/v1/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted());

        // 2. Fetch the feed for user 100
        mockMvc.perform(get("/api/v1/notifications")
                .header("X-User-Id", "100")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].typeCode", is("system_alert")))
                .andExpect(jsonPath("$.content[0].sourceService", is("test-service")))
                .andExpect(jsonPath("$.content[0].status", is("pending")));

        // 3. Verify unread count
        mockMvc.perform(get("/api/v1/notifications/unread-count")
                .header("X-User-Id", "100")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.unreadCount", is(1)));
    }

    @Test
    public void testUpdateStatus() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("message", "Another test notification");

        SendNotificationRequest request = new SendNotificationRequest(
            "system_alert",
            "test-service",
            "user",
            200L,
            null,
            payload,
            new HashMap<>(),
            3,
            null
        );

        mockMvc.perform(post("/api/v1/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted());

        // Get notification ID from feed
        String feedResponse = mockMvc.perform(get("/api/v1/notifications")
                .header("X-User-Id", "200")
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        // Safe parser extract id using quick index lookup
        int idIndex = feedResponse.indexOf("\"id\":");
        int commaIndex = feedResponse.indexOf(",", idIndex);
        String idStr = feedResponse.substring(idIndex + 5, commaIndex).trim();
        long notifId = Long.parseLong(idStr);

        // Update status to read
        UpdateNotificationStatusRequest updateReq = new UpdateNotificationStatusRequest("read");
        mockMvc.perform(put("/api/v1/notifications/" + notifId + "/status")
                .header("X-User-Id", "200")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk());

        // Verify unread count is now 0
        mockMvc.perform(get("/api/v1/notifications/unread-count")
                .header("X-User-Id", "200")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.unreadCount", is(0)));
    }

    @Test
    public void testRegisterDevice() throws Exception {
        RegisterDeviceRequest request = new RegisterDeviceRequest(
            "ios",
            "ios-push-token-12345",
            new HashMap<>(),
            "1.0.0",
            "17.4",
            new HashMap<>()
        );

        mockMvc.perform(post("/api/v1/notifications/devices")
                .header("X-User-Id", "300")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        assertEquals(1, deviceRepository.findByUserIdAndIsActiveTrue(300L).size());
    }

    @Test
    public void testBackgroundWorkerProcessesPendingDeliveries() throws Exception {
        ChannelDelivery delivery = new ChannelDelivery();
        delivery.setId(888L);
        delivery.setNotificationId(999L);
        delivery.setNotificationCreatedAt(java.time.Instant.now());
        delivery.setUserId(400L);
        delivery.setChannel("email");
        delivery.setProvider("sendgrid");
        delivery.setStatus("queued");
        delivery.setNextRetryAt(java.time.Instant.now().minusSeconds(10)); // ready for pickup
        delivery.setCreatedAt(java.time.Instant.now().minusSeconds(10));
        delivery.setUpdatedAt(java.time.Instant.now().minusSeconds(10));
        channelDeliveryRepository.save(delivery);

        // Act: trigger the scheduled delivery processor manually
        notificationBackgroundWorker.processDeliveries();

        // Assert: verify status updated to delivered
        List<ChannelDelivery> deliveries = channelDeliveryRepository.findAll();
        assertEquals(1, deliveries.size());
        assertEquals("delivered", deliveries.get(0).getStatus());
        assertNotNull(deliveries.get(0).getSentAt());
    }

    @Test
    public void testSendScheduledNotification() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("message", "This is a scheduled event");

        java.time.Instant futureTime = java.time.Instant.now().plus(1, java.time.temporal.ChronoUnit.HOURS);

        SendNotificationRequest request = new SendNotificationRequest(
            "system_alert",
            "test-service",
            "user",
            500L, // target user ID
            null,
            payload,
            new HashMap<>(),
            3,
            futureTime
        );

        // 1. Trigger the notification
        mockMvc.perform(post("/api/v1/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted());

        // 2. Fetch the feed for user 500 (should be empty because scheduled time is in the future)
        mockMvc.perform(get("/api/v1/notifications")
                .header("X-User-Id", "500")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)));

        // 3. Verify unread count is 0
        mockMvc.perform(get("/api/v1/notifications/unread-count")
                .header("X-User-Id", "500")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.unreadCount", is(0)));
    }
}
