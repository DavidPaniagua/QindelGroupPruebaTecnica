package com.paniagua.pricing.prices.infrastructure.in.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PriceControllerIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  private static final String BASE_URL = "/prices";

  @ParameterizedTest(name = "Test {index}: Date {0}, Product {1}, Brand {2} -> PriceList {3}, Price {6}")
  @CsvSource({
      //applicationDate, productId, brandId, expectedPriceList, expectedStartDate, expectedEndDate, expectedPrice, expectedCurrency
      "2020-06-14T10:00:00Z, 35455, 1, 1, 2020-06-14T00:00:00Z, 2020-12-31T23:59:59Z, 35.50, EUR",
      "2020-06-14T16:00:00Z, 35455, 1, 2, 2020-06-14T15:00:00Z, 2020-06-14T18:30:00Z, 25.45, EUR",
      "2020-06-14T21:00:00Z, 35455, 1, 1, 2020-06-14T00:00:00Z, 2020-12-31T23:59:59Z, 35.50, EUR",
      "2020-06-15T10:00:00Z, 35455, 1, 3, 2020-06-15T00:00:00Z, 2020-06-15T11:00:00Z, 30.50, EUR",
      "2020-06-16T21:00:00Z, 35455, 1, 4, 2020-06-15T16:00:00Z, 2020-12-31T23:59:59Z, 38.95, EUR"
  })
  void given_ValidParameters_when_GetPrice_then_ReturnsExpectedPrice(
      final String applicationDate,
      final Long productId,
      final Long brandId,
      final int expectedPriceList,
      final String expectedStartDate,
      final String expectedEndDate,
      final BigDecimal expectedPrice,
      final String expectedCurrency
  ) throws Exception {

    mockMvc.perform(get(BASE_URL)
            .param("applicationDate", applicationDate)
            .param("productId", String.valueOf(productId))
            .param("brandId", String.valueOf(brandId)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.productId").value(productId))
        .andExpect(jsonPath("$.brandId").value(brandId))
        .andExpect(jsonPath("$.priceList").value(expectedPriceList))
        .andExpect(jsonPath("$.startDate").value(expectedStartDate))
        .andExpect(jsonPath("$.endDate").value(expectedEndDate))
        .andExpect(jsonPath("$.price").value(expectedPrice.doubleValue()))
        .andExpect(jsonPath("$.currency").value(expectedCurrency));
  }

  @Test
  void given_RequestWithNoPrices_when_GetPrice_then_Returns404NotFound() throws Exception {
    mockMvc.perform(get(BASE_URL)
            .param("applicationDate", "1999-01-01T10:00:00Z")
            .param("productId", "35455")
            .param("brandId", "1"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status").value(404));
  }

  @Test
  void given_InvalidProductId_when_GetPrice_then_Returns400BadRequest() throws Exception {
    mockMvc.perform(get(BASE_URL)
            .param("applicationDate", "2020-06-14T10:00:00Z")
            .param("productId", "-1")
            .param("brandId", "1"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400));
  }

  @Test
  void given_MissingParameters_when_GetPrice_then_ReturnsBadRequest() throws Exception {
    mockMvc.perform(get(BASE_URL)
            .param("productId", "35455"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void given_MalformedDate_when_GetPrice_then_Returns400BadRequest() throws Exception {
    mockMvc.perform(get(BASE_URL)
            .param("applicationDate", "invalid-date")
            .param("productId", "35455")
            .param("brandId", "1"))
        .andExpect(status().isBadRequest());
  }
}