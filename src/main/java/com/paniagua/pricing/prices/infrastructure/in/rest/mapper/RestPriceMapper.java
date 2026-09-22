package com.paniagua.pricing.prices.infrastructure.in.rest.mapper;

import com.paniagua.pricing.prices.infrastructure.in.rest.dto.PriceResponse;
import com.paniagua.pricing.prices.domain.Price;
import org.springframework.stereotype.Component;

@Component
public class RestPriceMapper {

  public PriceResponse toDto(final Price price) {
    return new PriceResponse()
        .brandId(price.brandId().id())
        .productId(price.productId().id())
        .priceList(price.priceList())
        .startDate(price.startDate())
        .endDate(price.endDate())
        .price(price.price())
        .currency(price.currency());
  }
}