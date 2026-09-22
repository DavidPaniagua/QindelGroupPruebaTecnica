package com.paniagua.pricing.prices.domain.valueobject;

import com.paniagua.pricing.prices.domain.exception.InvalidIdException;

public record BrandId(Long id) {
  public BrandId {
    if (id == null || id <= 0) {
      throw new InvalidIdException("Brand ID must be a positive number");
    }
  }
}
