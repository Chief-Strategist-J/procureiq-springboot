package com.procureiq.springboot_app.features.fieldservice.dto.request;

import java.time.LocalDate;

public record ServiceCrewMemberRequest(
    Long serviceCrewId,
    Long serviceResourceId,
    String memberRole,
    LocalDate startDate,
    LocalDate endDate
) {}
