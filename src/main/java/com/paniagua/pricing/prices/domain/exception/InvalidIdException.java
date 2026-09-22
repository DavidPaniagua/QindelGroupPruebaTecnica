package com.paniagua.pricing.prices.domain.exception;

public class InvalidIdException extends IllegalArgumentException {

  public InvalidIdException(final String message) {
    super(message);
  }
}
