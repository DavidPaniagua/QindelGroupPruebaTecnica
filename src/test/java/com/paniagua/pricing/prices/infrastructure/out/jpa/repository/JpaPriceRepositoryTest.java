package com.paniagua.pricing.prices.infrastructure.out.jpa.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.paniagua.pricing.prices.infrastructure.out.jpa.entity.PriceEntity;
import java.time.OffsetDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class JpaPriceRepositoryTest {

  @Autowired
  private JpaPriceRepository repository;

  @Test
  void given_MultiplePricesApplyWithDifferentPriority_when_FindApplicablePrices_then_ReturnsHighestPriority() {
    Long brandId = 1L;
    Long productId = 35455L;
    OffsetDateTime applicationDate = OffsetDateTime.parse("2020-06-14T16:00:00Z");

    Optional<PriceEntity> result = repository.findApplicablePrices(
        brandId,
        productId,
        applicationDate,
        PageRequest.of(0, 1)
    ).stream().findFirst();

    assertThat(result).isPresent();
    assertThat(result.get().getPriceList()).isEqualTo(2);
    assertThat(result.get().getPriority()).isEqualTo(1);
    assertThat(result.get().getPrice()).isEqualByComparingTo("25.45");
  }
}