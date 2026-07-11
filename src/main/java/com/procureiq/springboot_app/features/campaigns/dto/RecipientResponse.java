package com.procureiq.springboot_app.features.campaigns.dto;

public record RecipientResponse(
    Long id,
    Long accountId,
    String name,
    String email,
    String phone
) {}
