package com.paniagua.pricing.prices.application.in.usecase;

import com.paniagua.pricing.prices.domain.Price;
import java.time.OffsetDateTime;

public interface FindApplicablePriceUseCase {

  Price find(final Long brandId, final Long productId, final OffsetDateTime applicationDate);
}
