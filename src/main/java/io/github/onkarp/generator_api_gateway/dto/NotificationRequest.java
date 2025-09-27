package io.github.onkarp.generator_api_gateway.dto;



public record NotificationRequest(
        String trackingNumber,
        String customerName,
        String customerSlug,
        String customerEmail
) {}

