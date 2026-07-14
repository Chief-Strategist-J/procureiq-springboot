package com.procureiq.springboot_app.features.campaigns.dto.request;

public record RecipientRequest(
    Long accountId,
    String name,
    String email,
    String phone
) {}
