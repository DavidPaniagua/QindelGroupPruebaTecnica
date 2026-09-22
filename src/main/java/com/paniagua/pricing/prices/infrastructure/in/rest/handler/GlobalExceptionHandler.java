package com.paniagua.pricing.prices.infrastructure.in.rest.handler;

import com.paniagua.pricing.prices.infrastructure.in.rest.dto.ErrorResponse;
import com.paniagua.pricing.prices.application.exception.PriceNotFoundException;
import com.paniagua.pricing.prices.domain.exception.InvalidIdException;
import java.time.OffsetDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(PriceNotFoundException.class)
  public ResponseEntity<ErrorResponse> handlePriceNotFoundException(
      final PriceNotFoundException ex,
      final WebRequest request
  ) {
    final ErrorResponse errorResponse = new ErrorResponse()
        .timestamp(OffsetDateTime.now())
        .status(HttpStatus.NOT_FOUND.value())
        .error(HttpStatus.NOT_FOUND.getReasonPhrase())
        .message(ex.getMessage())
        .path(extractPath(request));

    return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler({
      InvalidIdException.class,
      MethodArgumentTypeMismatchException.class,
      MissingServletRequestParameterException.class
  })
  public ResponseEntity<ErrorResponse> handleBadRequestExceptions(
      final Exception ex,
      final WebRequest request
  ) {
    final ErrorResponse errorResponse = new ErrorResponse()
        .timestamp(OffsetDateTime.now())
        .status(HttpStatus.BAD_REQUEST.value())
        .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
        .message(ex.getMessage())
        .path(extractPath(request));

    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGlobalException(
      final Exception ex,
      final WebRequest request
  ) {
    final ErrorResponse errorResponse = new ErrorResponse()
        .timestamp(OffsetDateTime.now())
        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
        .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
        .message("An unexpected error occurred.")
        .path(extractPath(request));

    return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  private String extractPath(final WebRequest request) {
    return request.getDescription(false).replace("uri=", "");
  }
}