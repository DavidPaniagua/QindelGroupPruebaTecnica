package com.paniagua.pricing.prices.infrastructure.out.jpa.mapper;

import com.paniagua.pricing.prices.domain.Price;
import com.paniagua.pricing.prices.domain.valueobject.BrandId;
import com.paniagua.pricing.prices.domain.valueobject.ProductId;
import com.paniagua.pricing.prices.infrastructure.out.jpa.entity.PriceEntity;
import org.springframework.stereotype.Component;

@Component
public class JpaPriceMapper {

  public Price toDomain(final PriceEntity priceEntity) {
    return new Price(
        new BrandId(priceEntity.getBrandId()),
        new ProductId(priceEntity.getProductId()),
        priceEntity.getPrice(),
        priceEntity.getCurrency(),
        priceEntity.getPriceList(),
        priceEntity.getPriority(),
        priceEntity.getStartDate(),
        priceEntity.getEndDate());
  }

  public PriceEntity toEntity(final Price price) {
    return PriceEntity.builder()
        .brandId(price.brandId().id())
        .productId(price.productId().id())
        .price(price.price())
        .currency(price.currency())
        .priceList(price.priceList())
        .priority(price.priority())
        .startDate(price.startDate())
        .endDate(price.endDate())
        .build();
  }
}
