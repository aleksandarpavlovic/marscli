package com.alexp.exception;

public class CacheException extends RuntimeException {
  public CacheException(String message) {
    super(message);
  }

  public CacheException(String message, Throwable t) {
    super(message, t);
  }
}
