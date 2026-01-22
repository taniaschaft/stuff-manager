package com.gamemanager.service;

import com.gamemanager.client.PublisherClient;
import com.gamemanager.dto.PublisherDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PublisherServiceTest {

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
