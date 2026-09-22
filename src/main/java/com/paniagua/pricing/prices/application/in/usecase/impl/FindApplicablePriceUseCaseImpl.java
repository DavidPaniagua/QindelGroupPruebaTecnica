package com.paniagua.pricing.prices.application.in.usecase.impl;

import com.paniagua.pricing.prices.application.in.usecase.FindApplicablePriceUseCase;
import com.paniagua.pricing.prices.application.out.PricePort;
import com.paniagua.pricing.prices.application.exception.PriceNotFoundException;
import com.paniagua.pricing.prices.domain.Price;
import com.paniagua.pricing.prices.domain.valueobject.BrandId;
import com.paniagua.pricing.prices.domain.valueobject.ProductId;
import java.time.OffsetDateTime;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FindApplicablePriceUseCaseImpl implements FindApplicablePriceUseCase {

  private final PricePort pricePort;

  private static final String ERROR_MESSAGE_PRICE_NOT_FOUND =
      "No applicable price found for brandId: %d, productId: %d on date: %s";

  @Override
  @Transactional(readOnly = true)
  @Cacheable(value = "applicablePrices", key = "{#brandId, #productId, #applicationDate}")
  public Price find(final Long brandId, final Long productId, final OffsetDateTime applicationDate) {
    final BrandId brandIdVo = new BrandId(brandId);
    final ProductId productIdVo = new ProductId(productId);

    Objects.requireNonNull(applicationDate, "Application date cannot be null");

    return pricePort.findApplicablePrice(brandIdVo, productIdVo, applicationDate)
        .orElseThrow(() -> new PriceNotFoundException(
            String.format(ERROR_MESSAGE_PRICE_NOT_FOUND,
                brandIdVo.id(), productIdVo.id(), applicationDate.toString())));
  }
}