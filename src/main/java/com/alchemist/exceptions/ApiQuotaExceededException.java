package com.alchemist.exceptions;

/** Exception for exceeding API Quota. */
public class ApiQuotaExceededException extends Exception {

  private static final long serialVersionUID = 1L;

  public ApiQuotaExceededException(String errorMessage) {
    super(errorMessage);
  }
}