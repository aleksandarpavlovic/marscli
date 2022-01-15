package com.alexp.exception;

public class CacheInitializationException extends RuntimeException {
  public CacheInitializationException(String message) {
    super(message);
  }

  public CacheInitializationException(String message, Throwable t) {
    super(message, t);
  }
}
