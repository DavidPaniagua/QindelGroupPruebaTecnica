package com.paniagua.pricing.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
@EnableConfigurationProperties(PricingCacheProperties.class)
@RequiredArgsConstructor
public class CacheConfig {

  private final PricingCacheProperties cacheProperties;

  @Bean
  public CacheManager cacheManager() {
    final CaffeineCacheManager cacheManager = new CaffeineCacheManager("applicablePrices");
    cacheManager.setCaffeine(Caffeine.newBuilder()
        .maximumSize(10_000)
        .expireAfterWrite(Duration.ofMinutes(cacheProperties.ttlMinutes()))
        .recordStats());
    return cacheManager;
  }
}