package com.edu.tutor_platform.rating.exception;

public class UnauthorizedRatingException extends RuntimeException {
    public UnauthorizedRatingException(String message) {
        super(message);
    }
}