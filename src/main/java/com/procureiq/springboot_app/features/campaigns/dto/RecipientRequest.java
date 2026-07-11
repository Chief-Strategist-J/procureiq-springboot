package com.procureiq.springboot_app.features.campaigns.dto;

public record RecipientRequest(
    Long accountId,
    String name,
    String email,
    String phone
) {}
