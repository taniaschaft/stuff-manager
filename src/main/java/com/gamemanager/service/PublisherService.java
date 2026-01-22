package com.gamemanager.service;

import com.gamemanager.client.PublisherClient;
import com.gamemanager.dto.PublisherDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Service
@EnableScheduling
public class PublisherService {
    private static final Logger logger = LoggerFactory.getLogger(PublisherService.class);
    
    private final PublisherClient publisherClient;
    private final Set<String> cachedPublisherIds = new HashSet<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private volatile boolean initialized = false;

    public PublisherService(PublisherClient publisherClient) {
        this.publisherClient = publisherClient;
        // Initialize cache on startup
        refreshPublisherCache();
    }

    /**
     * Validate if a publisher ID exists
     * Returns true if publisher exists or cache is empty/unavailable
     */
    public boolean validatePublisherId(String publisherId) {
        if (publisherId == null || publisherId.isBlank()) {
            logger.warn("Attempted to validate null or blank publisherId");
            return false;
        }

        lock.readLock().lock();
        try {
            // If cache is empty and initialized, validation fails
            if (cachedPublisherIds.isEmpty() && initialized) {
                logger.warn("Publisher cache is empty");
                return false;
            }

            // If cache is not initialized yet, attempt refresh
            if (!initialized) {
                lock.readLock().unlock();
                lock.writeLock().lock();
                try {
                    refreshPublisherCache();
                } finally {
                    lock.writeLock().unlock();
                    lock.readLock().lock();
                }
            }

            boolean exists = cachedPublisherIds.stream()
                .anyMatch(p -> p.equalsIgnoreCase(publisherId));
            
            logger.debug("Publisher validation for '{}': {}", publisherId, exists);
            return exists;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Refresh the publisher cache from publisher-manager service
     * Scheduled to run every 5 minutes (300000 ms)
     */
    @Scheduled(fixedRate = 300000, initialDelay = 0)
    public void refreshPublisherCache() {
        lock.writeLock().lock();
        try {
            logger.info("Refreshing publisher cache...");
            List<PublisherDTO> publishers = publisherClient.getAllPublishers();
            
            cachedPublisherIds.clear();
            publishers.forEach(p -> cachedPublisherIds.add(p.getId().toLowerCase()));
            
            initialized = true;
            logger.info("Publisher cache refreshed with {} publishers", cachedPublisherIds.size());
        } catch (Exception e) {
            logger.error("Failed to refresh publisher cache", e);
            // Keep existing cache on error if already initialized
            if (!initialized) {
                initialized = false;
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Get cached publisher count (for monitoring)
     */
    public int getCachedPublisherCount() {
        lock.readLock().lock();
        try {
            return cachedPublisherIds.size();
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Check if cache is initialized
     */
    public boolean isCacheInitialized() {
        lock.readLock().lock();
        try {
            return initialized;
        } finally {
            lock.readLock().unlock();
        }
    }
}
