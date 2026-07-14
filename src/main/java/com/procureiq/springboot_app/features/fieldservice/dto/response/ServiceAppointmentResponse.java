package com.procureiq.springboot_app.features.fieldservice.dto.response;

import java.time.Instant;
import java.util.Map;

public record ServiceAppointmentResponse(
    Long id,
    String parentRecordType,
    Long parentRecordId,
    Long accountId,
    Long contactId,
    Long serviceTerritoryId,
    Long workTypeId,
    String status,
    Instant scheduledStart,
    Instant scheduledEnd,
    Instant arrivalWindowStart,
    Instant arrivalWindowEnd,
    Integer durationMinutes,
    Map<String, Object> address,
    Instant createdAt
) {
    public static ServiceAppointmentResponse fromEntity(final com.procureiq.springboot_app.features.fieldservice.entity.ServiceAppointment entity) {
        return new ServiceAppointmentResponse(
            entity.getId(),
                    entity.getParentRecordType(),
                    entity.getParentRecordId(),
                    entity.getAccount() != null ? entity.getAccount().getId() : null,
                    entity.getContact() != null ? entity.getContact().getId() : null,
                    entity.getServiceTerritory() != null ? entity.getServiceTerritory().getId() : null,
                    entity.getWorkType() != null ? entity.getWorkType().getId() : null,
                    entity.getStatus(),
                    entity.getScheduledStart(),
                    entity.getScheduledEnd(),
                    entity.getArrivalWindowStart(),
                    entity.getArrivalWindowEnd(),
                    entity.getDurationMinutes(),
                    entity.getAddress(),
                    entity.getCreatedAt()
        );
    }
}
