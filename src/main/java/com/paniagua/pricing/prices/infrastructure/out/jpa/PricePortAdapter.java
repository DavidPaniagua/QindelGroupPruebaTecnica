package com.paniagua.pricing.prices.infrastructure.out.jpa;

import com.paniagua.pricing.prices.application.out.PricePort;
import com.paniagua.pricing.prices.domain.Price;
import com.paniagua.pricing.prices.domain.valueobject.BrandId;
import com.paniagua.pricing.prices.domain.valueobject.ProductId;
import com.paniagua.pricing.prices.infrastructure.out.jpa.mapper.JpaPriceMapper;
import com.paniagua.pricing.prices.infrastructure.out.jpa.repository.JpaPriceRepository;
import java.time.OffsetDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PricePortAdapter implements PricePort {

  private final JpaPriceRepository priceRepository;
  private final JpaPriceMapper priceMapper;

  @Override
  public Optional<Price> findApplicablePrice(
      final BrandId brandId,
      final ProductId productId,
      final OffsetDateTime applicationDate) {

    return priceRepository.findApplicablePrices(
            brandId.id(),
            productId.id(),
            applicationDate,
            PageRequest.of(0, 1))
        .stream()
        .findFirst()
        .map(priceMapper::toDomain);
  }
}