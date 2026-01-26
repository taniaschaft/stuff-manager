package com.gamemanager.service;

import com.gamemanager.client.PublisherClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PublisherService {
    private static final Logger logger = LoggerFactory.getLogger(PublisherService.class);
    
    private final PublisherClient publisherClient;

    public PublisherService(PublisherClient publisherClient) {
        this.publisherClient = publisherClient;
    }

    /**
     * Validate if a publisher ID exists by calling the publisher-manager service
     * Makes a fresh call to the publisher-manager for each validation
     */
    public boolean validatePublisherId(String publisherId) {
        if (publisherId == null || publisherId.isBlank()) {
            logger.warn("Attempted to validate null or blank publisherId");
            return false;
        }

        logger.debug("Validating publisher '{}' via publisher-manager service", publisherId);
        boolean exists = publisherClient.publisherExists(publisherId);
        logger.debug("Publisher validation for '{}': {}", publisherId, exists);
        return exists;
    }
}
