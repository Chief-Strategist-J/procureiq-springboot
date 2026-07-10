package com.procureiq.springboot_app.features.fieldservice.dto;

import java.time.LocalDate;

public record ServiceCrewMemberRequest(
    Long serviceCrewId,
    Long serviceResourceId,
    String memberRole,
    LocalDate startDate,
    LocalDate endDate
) {}
