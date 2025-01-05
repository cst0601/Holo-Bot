package com.alchemist.exceptions;

/** Exception for ArgParser when parsing results in error. */
public class ArgumentParseException extends Exception {
  private static final long serialVersionUID = 1L;

  public ArgumentParseException(String errorMessage) {
    super(errorMessage);
  }
}
