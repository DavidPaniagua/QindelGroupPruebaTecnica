package com.paniagua.pricing.prices.infrastructure.in.rest;

import com.paniagua.pricing.config.PricingCacheProperties;
import com.paniagua.pricing.prices.infrastructure.in.rest.api.PricesApi;
import com.paniagua.pricing.prices.infrastructure.in.rest.dto.PriceResponse;
import com.paniagua.pricing.prices.application.in.usecase.FindApplicablePriceUseCase;
import com.paniagua.pricing.prices.infrastructure.in.rest.mapper.RestPriceMapper;
import java.time.Duration;
import java.time.OffsetDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PriceController implements PricesApi {

  private final FindApplicablePriceUseCase findApplicablePriceUseCase;
  private final RestPriceMapper restPriceMapper;
  private final PricingCacheProperties cacheProperties;

  @Override
  public ResponseEntity<PriceResponse> getApplicablePrice(
      final OffsetDateTime applicationDate,
      final Long productId,
      final Long brandId
  ) {
    return ResponseEntity.ok()
        .cacheControl(CacheControl.maxAge(Duration.ofMinutes(cacheProperties.ttlMinutes())).cachePublic())
        .body(restPriceMapper.toDto(
            findApplicablePriceUseCase.find(brandId, productId, applicationDate)
        ));
  }
}