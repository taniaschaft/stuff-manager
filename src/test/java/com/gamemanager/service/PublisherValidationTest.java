package com.gamemanager.service;

import com.gamemanager.client.PublisherClient;
import com.gamemanager.dto.PublisherDTO;
import com.gamemanager.controller.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Comprehensive test suite for Publisher validation, PublisherService, and exception handling.
 */
@ExtendWith(MockitoExtension.class)
public class PublisherValidationTest {

    // ==================== PublisherService Tests ====================
    @Nested
    class PublisherServiceTests {

        @Mock
        private PublisherClient publisherClient;

        private PublisherService publisherService;

        @BeforeEach
        public void setUp() {
            publisherService = new PublisherService(publisherClient);
        }

        @Test
        public void testValidatePublisherIdExists() {
            // Arrange
            List<PublisherDTO> publishers = new ArrayList<>();
            publishers.add(new PublisherDTO("nintendo", "Nintendo"));
            publishers.add(new PublisherDTO("konami", "Konami"));
            
            when(publisherClient.getAllPublishers()).thenReturn(publishers);
            
            // Act & Assert
            assertTrue(publisherService.validatePublisherId("nintendo"));
            assertTrue(publisherService.validatePublisherId("konami"));
        }

        @Test
        public void testValidatePublisherIdCaseInsensitive() {
            // Arrange
            List<PublisherDTO> publishers = new ArrayList<>();
            publishers.add(new PublisherDTO("nintendo", "Nintendo"));
            
            when(publisherClient.getAllPublishers()).thenReturn(publishers);
            
            // Act & Assert - should match case-insensitively
            assertTrue(publisherService.validatePublisherId("NINTENDO"));
            assertTrue(publisherService.validatePublisherId("Nintendo"));
            assertTrue(publisherService.validatePublisherId("nintendo"));
        }

        @Test
        public void testValidatePublisherIdDoesNotExist() {
            // Arrange
            List<PublisherDTO> publishers = new ArrayList<>();
            publishers.add(new PublisherDTO("nintendo", "Nintendo"));
            
            when(publisherClient.getAllPublishers()).thenReturn(publishers);
            
            // Act & Assert
            assertFalse(publisherService.validatePublisherId("konami"));
        }

        @Test
        public void testValidatePublisherIdWithNullId() {
            // Act & Assert
            assertFalse(publisherService.validatePublisherId(null));
        }

        @Test
        public void testValidatePublisherIdWithBlankId() {
            // Act & Assert
            assertFalse(publisherService.validatePublisherId("   "));
            assertFalse(publisherService.validatePublisherId(""));
        }

        @Test
        public void testCacheInitializesOnConstruction() {
            // Arrange
            List<PublisherDTO> publishers = new ArrayList<>();
            publishers.add(new PublisherDTO("nintendo", "Nintendo"));
            
            when(publisherClient.getAllPublishers()).thenReturn(publishers);
            
            // Act
            PublisherService service = new PublisherService(publisherClient);
            
            // Assert
            assertTrue(service.isCacheInitialized());
            assertEquals(1, service.getCachedPublisherCount());
        }

        @Test
        public void testCacheWithEmptyPublisherList() {
            // Arrange
            when(publisherClient.getAllPublishers()).thenReturn(new ArrayList<>());
            
            // Act
            PublisherService service = new PublisherService(publisherClient);
            
            // Assert
            assertTrue(service.isCacheInitialized());
            assertEquals(0, service.getCachedPublisherCount());
        }

        @Test
        public void testValidatePublisherIdEmptyCacheFails() {
            // Arrange
            when(publisherClient.getAllPublishers()).thenReturn(new ArrayList<>());
            
            PublisherService service = new PublisherService(publisherClient);
            
            // Act & Assert - cache is empty and initialized, so validation should fail
            assertFalse(service.validatePublisherId("nintendo"));
        }

        @Test
        public void testGetCachedPublisherCount() {
            // Arrange
            List<PublisherDTO> publishers = new ArrayList<>();
            publishers.add(new PublisherDTO("nintendo", "Nintendo"));
            publishers.add(new PublisherDTO("konami", "Konami"));
            publishers.add(new PublisherDTO("sega", "Sega"));
            
            when(publisherClient.getAllPublishers()).thenReturn(publishers);
            
            // Act & Assert
            assertEquals(3, publisherService.getCachedPublisherCount());
        }

        @Test
        public void testRefreshPublisherCache() {
            // Arrange
            List<PublisherDTO> initialPublishers = new ArrayList<>();
            initialPublishers.add(new PublisherDTO("nintendo", "Nintendo"));
            
            List<PublisherDTO> updatedPublishers = new ArrayList<>();
            updatedPublishers.add(new PublisherDTO("nintendo", "Nintendo"));
            updatedPublishers.add(new PublisherDTO("konami", "Konami"));
            
            when(publisherClient.getAllPublishers())
                .thenReturn(initialPublishers)
                .thenReturn(updatedPublishers);
            
            PublisherService service = new PublisherService(publisherClient);
            assertEquals(1, service.getCachedPublisherCount());
            
            // Act - refresh cache
            service.refreshPublisherCache();
            
            // Assert
            assertEquals(2, service.getCachedPublisherCount());
            assertTrue(service.validatePublisherId("konami"));
        }

        @Test
        public void testPublisherServiceHandlesClientException() {
            // Arrange
            when(publisherClient.getAllPublishers())
                .thenThrow(new PublisherClient.PublisherServiceException("Service unavailable"));
            
            // Act & Assert - should not throw, should handle gracefully
            assertDoesNotThrow(() -> {
                PublisherService service = new PublisherService(publisherClient);
                assertFalse(service.isCacheInitialized());
            });
        }
    }

    // ==================== GlobalExceptionHandler Tests ====================
    @Nested
    class GlobalExceptionHandlerTests {

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
}
