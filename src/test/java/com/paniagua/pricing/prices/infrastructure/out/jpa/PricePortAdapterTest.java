package com.paniagua.pricing.prices.infrastructure.out.jpa;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.paniagua.pricing.prices.domain.Price;
import com.paniagua.pricing.prices.domain.valueobject.BrandId;
import com.paniagua.pricing.prices.domain.valueobject.ProductId;
import com.paniagua.pricing.prices.infrastructure.out.jpa.entity.PriceEntity;
import com.paniagua.pricing.prices.infrastructure.out.jpa.mapper.JpaPriceMapper;
import com.paniagua.pricing.prices.infrastructure.out.jpa.repository.JpaPriceRepository;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class PricePortAdapterTest {

  @Mock
  private JpaPriceRepository priceRepository;

  @Mock
  private JpaPriceMapper priceMapper;

  @InjectMocks
  private PricePortAdapter adapter;

  @Test
  void given_RepositoryReturnsMultiplePrices_when_FindApplicablePrice_then_RequestsOnlyFirstPage() {
    final Long brandId = 1L;
    final Long productId = 35455L;
    final OffsetDateTime applicationDate = OffsetDateTime.parse("2020-06-14T16:00:00Z");

    final PriceEntity entity = PriceEntity.builder()
        .brandId(brandId)
        .productId(productId)
        .price(new BigDecimal("25.45"))
        .currency("EUR")
        .priceList(2)
        .priority(1)
        .startDate(OffsetDateTime.parse("2020-06-14T15:00:00Z"))
        .endDate(OffsetDateTime.parse("2020-06-14T18:30:00Z"))
        .build();

    final Price expectedPrice = new Price(
        new BrandId(brandId),
        new ProductId(productId),
        new BigDecimal("25.45"),
        "EUR",
        2,
        1,
        OffsetDateTime.parse("2020-06-14T15:00:00Z"),
        OffsetDateTime.parse("2020-06-14T18:30:00Z")
    );

    given(priceRepository.findApplicablePrices(eq(brandId), eq(productId), eq(applicationDate), any(Pageable.class)))
        .willReturn(List.of(entity));
    given(priceMapper.toDomain(entity)).willReturn(expectedPrice);

    final Optional<Price> result = adapter.findApplicablePrice(
        new BrandId(brandId), new ProductId(productId), applicationDate);

    assertThat(result).contains(expectedPrice);
    verify(priceRepository).findApplicablePrices(
        eq(brandId), eq(productId), eq(applicationDate),
        argThat(pageable -> pageable.getPageSize() == 1));
  }

  @Test
  void given_RepositoryReturnsEmptyList_when_FindApplicablePrice_then_ReturnsEmptyOptional() {
    final BrandId brandId = new BrandId(1L);
    final ProductId productId = new ProductId(35455L);
    final OffsetDateTime applicationDate = OffsetDateTime.parse("1999-01-01T10:00:00Z");

    given(priceRepository.findApplicablePrices(any(), any(), any(), any()))
        .willReturn(List.of());

    final Optional<Price> result = adapter.findApplicablePrice(brandId, productId, applicationDate);

    assertThat(result).isEmpty();
    verify(priceMapper, never()).toDomain(any());
  }
}