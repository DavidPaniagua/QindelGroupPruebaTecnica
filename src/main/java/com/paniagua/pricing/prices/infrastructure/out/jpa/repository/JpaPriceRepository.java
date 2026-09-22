package com.paniagua.pricing.prices.infrastructure.out.jpa.repository;

import com.paniagua.pricing.prices.infrastructure.out.jpa.entity.PriceEntity;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JpaPriceRepository extends JpaRepository<PriceEntity, Long> {

  @Query("""
        SELECT p FROM PriceEntity p 
        WHERE p.brandId = :brandId 
          AND p.productId = :productId 
          AND :appDate >= p.startDate 
          AND :appDate <= p.endDate 
        ORDER BY p.priority DESC, p.price DESC
        """)
  List<PriceEntity> findApplicablePrices(
      @Param("brandId") final Long brandId,
      @Param("productId") final Long productId,
      @Param("appDate") final OffsetDateTime appDate,
      final Pageable pageable
  );
}