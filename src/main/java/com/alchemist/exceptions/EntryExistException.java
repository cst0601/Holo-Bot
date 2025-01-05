package com.alchemist.exceptions;

/** Exception for searching user membership verification in DB. */
public class EntryExistException extends Exception {
  private static final long serialVersionUID = 1L;

  public EntryExistException(String errorMessage) {
    super(errorMessage);
  }
}
