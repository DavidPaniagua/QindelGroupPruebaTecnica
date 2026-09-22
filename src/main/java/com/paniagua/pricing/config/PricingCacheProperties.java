package com.paniagua.pricing.config;

import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "pricing.cache")
public record PricingCacheProperties(
    @Positive long ttlMinutes
) {
}