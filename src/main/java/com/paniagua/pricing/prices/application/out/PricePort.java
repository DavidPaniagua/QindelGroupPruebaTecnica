package com.paniagua.pricing.prices.application.out;

import com.paniagua.pricing.prices.domain.Price;
import com.paniagua.pricing.prices.domain.valueobject.BrandId;
import com.paniagua.pricing.prices.domain.valueobject.ProductId;
import java.time.OffsetDateTime;
import java.util.Optional;

public interface PricePort {

  Optional<Price> findApplicablePrice(
      final BrandId brandId,
      final ProductId productId,
      final OffsetDateTime applicationDate
  );
}
