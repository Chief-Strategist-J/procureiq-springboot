package com.procureiq.springboot_app.features.fieldservice.dto.response;

import java.time.LocalDate;

public record ServiceCrewMemberResponse(
    Long id,
    Long serviceCrewId,
    Long serviceResourceId,
    String memberRole,
    LocalDate startDate,
    LocalDate endDate
) {}
