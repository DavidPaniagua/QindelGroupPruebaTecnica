package com.paniagua.pricing.prices.application.in.usecase.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.paniagua.pricing.prices.application.out.PricePort;
import com.paniagua.pricing.prices.application.exception.PriceNotFoundException;
import com.paniagua.pricing.prices.domain.Price;
import com.paniagua.pricing.prices.domain.exception.InvalidIdException;
import com.paniagua.pricing.prices.domain.valueobject.BrandId;
import com.paniagua.pricing.prices.domain.valueobject.ProductId;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FindApplicablePriceUseCaseImplTest {

  @Mock
  private PricePort pricePort;

  @InjectMocks
  private FindApplicablePriceUseCaseImpl useCase;

  @Test
  void given_ExistingPrice_when_Find_then_ReturnsPrice() {
    Long brandId = 1L;
    Long productId = 35455L;
    OffsetDateTime applicableDate = OffsetDateTime.parse("2020-06-14T10:00:00Z");

    Price expectedPrice = new Price(
        new BrandId(brandId),
        new ProductId(productId),
        new BigDecimal("35.50"),
        "EUR",
        1,
        0,
        OffsetDateTime.parse("2020-06-14T00:00:00Z"),
        OffsetDateTime.parse("2020-12-31T23:59:59Z")
    );

    given(pricePort.findApplicablePrice(any(BrandId.class), any(ProductId.class), any(OffsetDateTime.class)))
        .willReturn(Optional.of(expectedPrice));

    Price actualPrice = useCase.find(brandId, productId, applicableDate);

    assertThat(actualPrice).isNotNull();
    assertThat(actualPrice.price()).isEqualTo(new BigDecimal("35.50"));

    verify(pricePort).findApplicablePrice(
        new BrandId(brandId),
        new ProductId(productId),
        applicableDate
    );
  }

  @Test
  void given_NoMatchingPrice_when_Find_then_ThrowsPriceNotFoundException() {
    Long brandId = 1L;
    Long productId = 35455L;
    OffsetDateTime applicableDate = OffsetDateTime.parse("2020-06-14T10:00:00Z");

    given(pricePort.findApplicablePrice(any(BrandId.class), any(ProductId.class), any(OffsetDateTime.class)))
        .willReturn(Optional.empty());

    assertThatThrownBy(() -> useCase.find(brandId, productId, applicableDate))
        .isInstanceOf(PriceNotFoundException.class)
        .hasMessageContaining("No applicable price found for brandId: 1, productId: 35455 on date: 2020-06-14T10:00Z");
  }

  @Test
  void given_NullBrandId_when_Find_then_ThrowsIdFormatException() {
    Long invalidBrandId = null;
    Long validProductId = 35455L;
    OffsetDateTime applicableDate = OffsetDateTime.parse("2020-06-14T10:00:00Z");

    assertThatThrownBy(() -> useCase.find(invalidBrandId, validProductId, applicableDate))
        .isInstanceOf(InvalidIdException.class)
        .hasMessage("Brand ID must be a positive number");
  }

  @Test
  void given_NegativeBrandId_when_Find_then_ThrowsIdFormatException() {
    Long invalidBrandId = -1L;
    Long validProductId = 35455L;
    OffsetDateTime applicableDate = OffsetDateTime.parse("2020-06-14T10:00:00Z");

    assertThatThrownBy(() -> useCase.find(invalidBrandId, validProductId, applicableDate))
        .isInstanceOf(InvalidIdException.class)
        .hasMessage("Brand ID must be a positive number");
  }

  @Test
  void given_NullProductId_when_Find_then_ThrowsIdFormatException() {
    Long validBrandId = 1L;
    Long invalidProductId = null;
    OffsetDateTime applicableDate = OffsetDateTime.parse("2020-06-14T10:00:00Z");

    assertThatThrownBy(() -> useCase.find(validBrandId, invalidProductId, applicableDate))
        .isInstanceOf(InvalidIdException.class)
        .hasMessage("Product ID must be a positive number");
  }

  @Test
  void given_ZeroProductId_when_Find_then_ThrowsIdFormatException() {
    Long validBrandId = 1L;
    Long invalidProductId = 0L;
    OffsetDateTime applicableDate = OffsetDateTime.parse("2020-06-14T10:00:00Z");

    assertThatThrownBy(() -> useCase.find(validBrandId, invalidProductId, applicableDate))
        .isInstanceOf(InvalidIdException.class)
        .hasMessage("Product ID must be a positive number");
  }

  @Test
  void given_NullApplicationDate_when_Find_then_ThrowsNullPointerException() {
    Long validBrandId = 1L;
    Long validProductId = 35455L;
    OffsetDateTime nullApplicableDate = null;

    assertThatThrownBy(() -> useCase.find(validBrandId, validProductId, nullApplicableDate))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("Application date cannot be null");
  }
}