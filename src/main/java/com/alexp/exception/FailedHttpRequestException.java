package com.alexp.exception;

public class FailedHttpRequestException extends RuntimeException {
  public FailedHttpRequestException(String message) {
    super(message);
  }

  public FailedHttpRequestException(String message, Throwable t) {
    super(message, t);
  }
}
