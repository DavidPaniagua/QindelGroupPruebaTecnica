package com.paniagua.pricing.prices.domain.valueobject;

import com.paniagua.pricing.prices.domain.exception.InvalidIdException;

public record ProductId(Long id) {
  public ProductId {
    if (id == null || id <= 0) {
      throw new InvalidIdException("Product ID must be a positive number");
    }
  }
}
