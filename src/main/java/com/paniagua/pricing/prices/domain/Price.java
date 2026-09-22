package com.paniagua.pricing.prices.domain;

import com.paniagua.pricing.prices.domain.valueobject.BrandId;
import com.paniagua.pricing.prices.domain.valueobject.ProductId;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Objects;

public record Price(
    BrandId brandId,
    ProductId productId,
    BigDecimal price,
    String currency,
    Integer priceList,
    Integer priority,
    OffsetDateTime startDate,
    OffsetDateTime endDate
) {
  public Price {
      Objects.requireNonNull(brandId, "BrandId cannot be null.");
      Objects.requireNonNull(productId, "ProductId cannot be null.");
      Objects.requireNonNull(price, "Price amount cannot be null.");
      Objects.requireNonNull(startDate, "Start date cannot be null.");
      Objects.requireNonNull(endDate, "End date cannot be null.");
      Objects.requireNonNull(currency, "Currency cannot be null.");
      Objects.requireNonNull(priceList, "Price list ID cannot be null.");
      Objects.requireNonNull(priority, "Priority cannot be null.");

      if (price.compareTo(BigDecimal.ZERO) < 0) {
        throw new IllegalArgumentException("Price amount cannot be negative.");
      }

      if (currency.isBlank()) {
        throw new IllegalArgumentException("Currency cannot be empty.");
      }

      if (startDate.isAfter(endDate)) {
        throw new IllegalArgumentException("Start date cannot be after end date.");
      }
  }
}