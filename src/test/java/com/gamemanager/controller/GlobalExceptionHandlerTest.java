package com.gamemanager.controller;

import com.gamemanager.service.InvalidPublisherException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    public void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    public void testHandleInvalidPublisherException() {
        // Arrange
        InvalidPublisherException exception = new InvalidPublisherException("Publisher 'unknown' is not registered");
        
        // Act
        ResponseEntity<Map<String, String>> response = exceptionHandler.handleInvalidPublisher(exception);
        
        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("error"));
        assertTrue(response.getBody().containsKey("status"));
        assertEquals("INVALID_PUBLISHER", response.getBody().get("status"));
        assertTrue(response.getBody().get("error").contains("unknown"));
    }

    @Test
    public void testHandleGenericException() {
        // Arrange
        Exception exception = new RuntimeException("Unexpected error occurred");
        
        // Act
        ResponseEntity<Map<String, String>> response = exceptionHandler.handleGenericException(exception);
        
        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("error"));
        assertTrue(response.getBody().containsKey("message"));
        assertEquals("An unexpected error occurred", response.getBody().get("error"));
    }

    @Test
    public void testHandleInvalidPublisherExceptionWithDifferentMessage() {
        // Arrange
        InvalidPublisherException exception = new InvalidPublisherException("Custom error message");
        
        // Act
        ResponseEntity<Map<String, String>> response = exceptionHandler.handleInvalidPublisher(exception);
        
        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Custom error message", response.getBody().get("error"));
    }
}
