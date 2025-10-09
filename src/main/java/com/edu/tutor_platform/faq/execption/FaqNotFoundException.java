package com.edu.tutor_platform.faq.execption;

public class FaqNotFoundException extends RuntimeException {
    public FaqNotFoundException(String message) {
        super(message);
    }
}
