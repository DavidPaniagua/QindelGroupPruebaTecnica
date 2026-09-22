package com.paniagua.pricing.prices.infrastructure.in.rest.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.paniagua.pricing.prices.infrastructure.in.rest.dto.PriceResponse;
import com.paniagua.pricing.prices.domain.Price;
import com.paniagua.pricing.prices.domain.valueobject.BrandId;
import com.paniagua.pricing.prices.domain.valueobject.ProductId;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.Test;

class RestPriceMapperTest {

  private final RestPriceMapper mapper = new RestPriceMapper();

  @Test
  void given_Price_when_ToDto_then_MapsAllFieldsCorrectly() {
    Price price = new Price(
        new BrandId(1L),
        new ProductId(35455L),
        new BigDecimal("35.50"),
        "EUR",
        1,
        0,
        OffsetDateTime.parse("2020-06-14T00:00:00Z"),
        OffsetDateTime.parse("2020-12-31T23:59:59Z")
    );

    PriceResponse response = mapper.toDto(price);

    assertThat(response.getBrandId()).isEqualTo(1L);
    assertThat(response.getProductId()).isEqualTo(35455L);
    assertThat(response.getPrice()).isEqualTo(new BigDecimal("35.50"));
    assertThat(response.getCurrency()).isEqualTo("EUR");
    assertThat(response.getPriceList()).isEqualTo(1);
    assertThat(response.getStartDate()).isEqualTo(OffsetDateTime.parse("2020-06-14T00:00:00Z"));
    assertThat(response.getEndDate()).isEqualTo(OffsetDateTime.parse("2020-12-31T23:59:59Z"));
  }
}