package com.paniagua.pricing.prices.infrastructure.out.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "PRICES")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PriceEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "BRAND_ID", nullable = false)
  private Long brandId;

  @Column(name = "PRODUCT_ID", nullable = false)
  private Long productId;

  @Column(name = "PRICE", nullable = false)
  private BigDecimal price;

  @Column(name = "CURR", nullable = false)
  private String currency;

  @Column(name = "PRICE_LIST", nullable = false)
  private Integer priceList;

  @Column(name = "PRIORITY", nullable = false)
  private Integer priority;

  @Column(name = "START_DATE", nullable = false)
  private OffsetDateTime startDate;

  @Column(name = "END_DATE", nullable = false)
  private OffsetDateTime endDate;
}