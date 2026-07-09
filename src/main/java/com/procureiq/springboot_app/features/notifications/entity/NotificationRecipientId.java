package com.procureiq.springboot_app.features.notifications.entity;

import java.io.Serializable;
import java.util.Objects;

public class NotificationRecipientId implements Serializable {
    private Long userId;
    private Long notificationId;

    public NotificationRecipientId() {}

    public NotificationRecipientId(Long userId, Long notificationId) {
        this.userId = userId;
        this.notificationId = notificationId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(Long notificationId) {
        this.notificationId = notificationId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotificationRecipientId that = (NotificationRecipientId) o;
        return Objects.equals(userId, that.userId) && Objects.equals(notificationId, that.notificationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, notificationId);
    }
}
