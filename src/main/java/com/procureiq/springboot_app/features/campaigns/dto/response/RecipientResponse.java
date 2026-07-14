package com.procureiq.springboot_app.features.campaigns.dto.response;

public record RecipientResponse(
    Long id,
    Long accountId,
    String name,
    String email,
    String phone
) {}
