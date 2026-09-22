package com.paniagua.pricing.prices.infrastructure.out.jpa.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.paniagua.pricing.prices.domain.Price;
import com.paniagua.pricing.prices.domain.valueobject.BrandId;
import com.paniagua.pricing.prices.domain.valueobject.ProductId;
import com.paniagua.pricing.prices.infrastructure.out.jpa.entity.PriceEntity;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.Test;

class JpaPriceMapperTest {

  private final JpaPriceMapper mapper = new JpaPriceMapper();

  @Test
  void given_PriceEntity_when_ToDomain_then_MapsAllFieldsCorrectly() {
    PriceEntity entity = PriceEntity.builder()
        .id(100L)
        .brandId(1L)
        .productId(35455L)
        .price(new BigDecimal("35.50"))
        .currency("EUR")
        .priceList(1)
        .priority(0)
        .startDate(OffsetDateTime.parse("2020-06-14T00:00:00Z"))
        .endDate(OffsetDateTime.parse("2020-12-31T23:59:59Z"))
        .build();

    Price domain = mapper.toDomain(entity);

    assertThat(domain.brandId().id()).isEqualTo(1L);
    assertThat(domain.productId().id()).isEqualTo(35455L);
    assertThat(domain.price()).isEqualTo(new BigDecimal("35.50"));
    assertThat(domain.currency()).isEqualTo("EUR");
    assertThat(domain.priceList()).isEqualTo(1);
    assertThat(domain.priority()).isEqualTo(0);
    assertThat(domain.startDate()).isEqualTo(OffsetDateTime.parse("2020-06-14T00:00:00Z"));
    assertThat(domain.endDate()).isEqualTo(OffsetDateTime.parse("2020-12-31T23:59:59Z"));
  }

  @Test
  void given_Price_when_ToEntity_then_MapsAllFieldsCorrectly() {
    Price domain = new Price(
        new BrandId(1L),
        new ProductId(35455L),
        new BigDecimal("35.50"),
        "EUR",
        1,
        0,
        OffsetDateTime.parse("2020-06-14T00:00:00Z"),
        OffsetDateTime.parse("2020-12-31T23:59:59Z")
    );

    PriceEntity entity = mapper.toEntity(domain);

    assertThat(entity.getBrandId()).isEqualTo(1L);
    assertThat(entity.getProductId()).isEqualTo(35455L);
    assertThat(entity.getPrice()).isEqualTo(new BigDecimal("35.50"));
    assertThat(entity.getCurrency()).isEqualTo("EUR");
    assertThat(entity.getPriceList()).isEqualTo(1);
    assertThat(entity.getPriority()).isEqualTo(0);
    assertThat(entity.getStartDate()).isEqualTo(OffsetDateTime.parse("2020-06-14T00:00:00Z"));
    assertThat(entity.getEndDate()).isEqualTo(OffsetDateTime.parse("2020-12-31T23:59:59Z"));
    assertThat(entity.getId()).isNull();
  }
}