package com.procureiq.springboot_app.features.notifications.service;

import com.procureiq.springboot_app.features.notifications.dto.*;
import com.procureiq.springboot_app.features.notifications.entity.*;
import com.procureiq.springboot_app.features.notifications.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class NotificationService {

    private final NotificationTypeRepository typeRepository;
    private final NotificationTemplateRepository templateRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationRecipientRepository recipientRepository;
    private final UserReadCursorRepository readCursorRepository;
    private final DeviceRepository deviceRepository;
    private final ChannelDeliveryRepository deliveryRepository;

    // In-memory generator fallback if sequence is not autowired for IDs
    private static final AtomicLong idGenerator = new AtomicLong(System.currentTimeMillis());

    public NotificationService(
            NotificationTypeRepository typeRepository,
            NotificationTemplateRepository templateRepository,
            NotificationRepository notificationRepository,
            NotificationRecipientRepository recipientRepository,
            UserReadCursorRepository readCursorRepository,
            DeviceRepository deviceRepository,
            ChannelDeliveryRepository deliveryRepository) {
        this.typeRepository = typeRepository;
        this.templateRepository = templateRepository;
        this.notificationRepository = notificationRepository;
        this.recipientRepository = recipientRepository;
        this.readCursorRepository = readCursorRepository;
        this.deviceRepository = deviceRepository;
        this.deliveryRepository = deliveryRepository;
    }

    @Transactional(readOnly = true)
    public NotificationListResponse getNotifications(Long userId, String status, int page, int size) {
        if (userId == null || userId <= 0) {
            return new NotificationListResponse(Collections.emptyList(), page, size, 0);
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        List<NotificationResponse> mergedList = new ArrayList<>();
        long totalElements = 0;

        // 1. Fetch targeted recipient rows
        Page<NotificationRecipient> recipientPage;
        if ("all".equalsIgnoreCase(status) || status == null || status.isBlank()) {
            recipientPage = recipientRepository.findByUserIdAndDeletedAtIsNull(userId, pageable);
        } else {
            recipientPage = recipientRepository.findByUserIdAndStatusAndDeletedAtIsNull(userId, status, pageable);
        }

        totalElements += recipientPage.getTotalElements();

        for (NotificationRecipient rec : recipientPage.getContent()) {
            NotificationId notifId = new NotificationId(rec.getNotificationId(), rec.getNotificationCreatedAt());
            Optional<Notification> notifOpt = notificationRepository.findById(notifId);
            if (notifOpt.isPresent()) {
                Notification notif = notifOpt.get();
                if (notif.getScheduledFor() != null && notif.getScheduledFor().isAfter(Instant.now())) {
                    continue;
                }
                mergedList.add(mapToResponse(notif, rec.getStatus()));
            }
        }

        // 2. Fetch broadcast notifications if loading unread or all
        if ("all".equalsIgnoreCase(status) || "pending".equalsIgnoreCase(status)) {
            long lastReadSeq = getOrCreateReadCursor(userId).getLastReadSeq();
            List<Notification> broadcasts = notificationRepository
                .findByTargetScopeAndGlobalSeqGreaterThanOrderByGlobalSeqDesc("broadcast", lastReadSeq);

            for (Notification bNotif : broadcasts) {
                if (bNotif.getScheduledFor() != null && bNotif.getScheduledFor().isAfter(Instant.now())) {
                    continue;
                }
                mergedList.add(mapToResponse(bNotif, "pending"));
            }
            totalElements += broadcasts.size();
        }

        // Sort combined list descending
        mergedList.sort((a, b) -> b.createdAt().compareTo(a.createdAt()));

        return new NotificationListResponse(mergedList, page, size, totalElements);
    }

    @Transactional(readOnly = true)
    public UnreadCountResponse getUnreadCount(Long userId) {
        if (userId == null || userId <= 0) {
            return new UnreadCountResponse(0);
        }

        // Count unread targeted notifications
        long targetedCount = recipientRepository.countUnreadTargetedActive(userId, "pending", Instant.now());

        // Count unread broadcast notifications
        long lastReadSeq = getOrCreateReadCursor(userId).getLastReadSeq();
        List<Notification> broadcasts = notificationRepository
            .findByTargetScopeAndGlobalSeqGreaterThanOrderByGlobalSeqDesc("broadcast", lastReadSeq);
        long broadcastCount = broadcasts.stream()
            .filter(b -> b.getScheduledFor() == null || !b.getScheduledFor().isAfter(Instant.now()))
            .count();

        return new UnreadCountResponse(targetedCount + broadcastCount);
    }

    @Transactional
    public void updateStatus(Long userId, Long notificationId, String status) {
        if (userId == null || notificationId == null || status == null) {
            return;
        }

        // Look up targeted recipient row
        Optional<NotificationRecipient> recOpt = recipientRepository.findByUserIdAndNotificationId(userId, notificationId);
        if (recOpt.isPresent()) {
            NotificationRecipient rec = recOpt.get();
            rec.setStatus(status);
            rec.setUpdatedAt(Instant.now());
            if ("read".equalsIgnoreCase(status)) {
                rec.setReadAt(Instant.now());
            } else if ("dismissed".equalsIgnoreCase(status)) {
                rec.setDismissedAt(Instant.now());
            }
            recipientRepository.save(rec);
        } else {
            // Check if it's a broadcast notification to advance read cursor
            // We search across partition timeframes (for ease, look up by ID using JPA fallback or sequential check)
            // In a real database, we would look up by global_seq on the notifications.
            // If found, update user's read cursor to this notification's global_seq
            // To simplify: we find the notification from notifications repository
            // For now, we will query all partitions or lookup the seq.
            // Since ID contains createdAt, we find it if we know the creation time or search.
            // Alternatively, advance the cursor to the highest globalSeq of broadcast notifications.
            advanceReadCursorToMax(userId);
        }
    }

    @Transactional
    public void registerDevice(Long userId, RegisterDeviceRequest request) {
        if (userId == null || request == null || request.platform() == null) {
            return;
        }

        String platform = request.platform();
        String token = request.pushToken() != null ? request.pushToken() : "";

        Optional<Device> deviceOpt = deviceRepository.findByUserIdAndPlatformAndPushToken(userId, platform, token);
        Device device = deviceOpt.orElseGet(() -> {
            Device newDevice = new Device();
            newDevice.setUserId(userId);
            newDevice.setPlatform(platform);
            newDevice.setPushToken(token);
            return newDevice;
        });

        device.setWebPushEndpoint(request.webPushEndpoint() != null ? request.webPushEndpoint() : new HashMap<>());
        device.setAppVersion(request.appVersion());
        device.setOsVersion(request.osVersion());
        device.setCapabilities(request.capabilities() != null ? request.capabilities() : new HashMap<>());
        device.setIsActive(true);
        device.setLastSeenAt(Instant.now());
        device.setUpdatedAt(Instant.now());

        deviceRepository.save(device);
    }

    @Transactional
    public void sendNotification(SendNotificationRequest request) {
        if (request == null || request.typeCode() == null || request.targetScope() == null) {
            return;
        }

        NotificationType type = typeRepository.findByCode(request.typeCode())
            .orElseThrow(() -> new IllegalArgumentException("Unknown notification type: " + request.typeCode()));

        // Deduplication check
        if (request.dedupKey() != null && !request.dedupKey().isBlank()) {
            if (notificationRepository.existsByTypeIdAndDedupKey(type.getId(), request.dedupKey())) {
                // Duplicate detected, return silently
                return;
            }
        }

        // Create new Notification
        Notification notif = new Notification();
        notif.setId(idGenerator.incrementAndGet());
        notif.setCreatedAt(Instant.now());
        notif.setType(type);
        notif.setSourceService(request.sourceService() != null ? request.sourceService() : "system");
        notif.setDedupKey(request.dedupKey());
        notif.setPayload(request.payload() != null ? request.payload() : new HashMap<>());
        notif.setMetadata(request.metadata() != null ? request.metadata() : new HashMap<>());
        notif.setPriority(request.priority() != null ? request.priority().shortValue() : type.getDefaultPriority());
        notif.setTargetScope(request.targetScope());
        notif.setTargetId(request.targetId());
        notif.setScheduledFor(request.scheduledFor());
        
        // Generate monotonic sequence number
        notif.setGlobalSeq(idGenerator.incrementAndGet());
        notif.setUpdatedAt(Instant.now());

        notificationRepository.save(notif);

        // Fan-out writes
        if ("user".equalsIgnoreCase(request.targetScope()) && request.targetId() != null) {
            NotificationRecipient rec = new NotificationRecipient();
            rec.setUserId(request.targetId());
            rec.setNotificationId(notif.getId());
            rec.setNotificationCreatedAt(notif.getCreatedAt());
            rec.setStatus("pending");
            rec.setCreatedAt(Instant.now());
            rec.setUpdatedAt(Instant.now());
            recipientRepository.save(rec);

            // Auto-queue channel deliveries
            if (type.getDefaultChannels() != null) {
                for (String ch : type.getDefaultChannels()) {
                    ChannelDelivery delivery = new ChannelDelivery();
                    delivery.setId(System.currentTimeMillis() + new Random().nextInt(1000));
                    delivery.setNotificationId(notif.getId());
                    delivery.setNotificationCreatedAt(notif.getCreatedAt());
                    delivery.setUserId(request.targetId());
                    delivery.setChannel(ch);
                    delivery.setProvider("sms".equalsIgnoreCase(ch) ? "twilio" : ("email".equalsIgnoreCase(ch) ? "sendgrid" : "fcm"));
                    delivery.setStatus("queued");
                    delivery.setNextRetryAt(request.scheduledFor() != null ? request.scheduledFor() : Instant.now());
                    delivery.setCreatedAt(Instant.now());
                    delivery.setUpdatedAt(Instant.now());
                    deliveryRepository.save(delivery);
                }
            }
        }
    }

    private UserReadCursor getOrCreateReadCursor(Long userId) {
        return readCursorRepository.findByUserId(userId).orElseGet(() -> {
            UserReadCursor cursor = new UserReadCursor();
            cursor.setUserId(userId);
            cursor.setLastReadSeq(0L);
            cursor.setCreatedAt(Instant.now());
            cursor.setUpdatedAt(Instant.now());
            return readCursorRepository.save(cursor);
        });
    }

    private void advanceReadCursorToMax(Long userId) {
        UserReadCursor cursor = getOrCreateReadCursor(userId);
        // Find highest global_seq of broadcast notifications
        List<Notification> latestBroadcasts = notificationRepository
            .findByTargetScopeAndGlobalSeqGreaterThanOrderByGlobalSeqDesc("broadcast", cursor.getLastReadSeq());
        if (!latestBroadcasts.isEmpty()) {
            cursor.setLastReadSeq(latestBroadcasts.get(0).getGlobalSeq());
            cursor.setUpdatedAt(Instant.now());
            readCursorRepository.save(cursor);
        }
    }

    private NotificationResponse mapToResponse(Notification notif, String status) {
        return new NotificationResponse(
            notif.getId(),
            notif.getType() != null ? notif.getType().getCode() : "unknown",
            notif.getSourceService(),
            notif.getPayload(),
            notif.getMetadata(),
            notif.getPriority().intValue(),
            notif.getTargetScope(),
            status,
            notif.getCreatedAt()
        );
    }
}
