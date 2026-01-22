package com.gamemanager.service;

public class InvalidPublisherException extends RuntimeException {
    public InvalidPublisherException(String message) {
        super(message);
    }

    public InvalidPublisherException(String message, Throwable cause) {
        super(message, cause);
    }
}
