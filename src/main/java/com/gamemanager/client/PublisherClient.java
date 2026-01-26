package com.gamemanager.client;

import com.gamemanager.dto.PublisherDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

import java.util.Arrays;
import java.util.List;

@Component
public class PublisherClient {
    private static final Logger logger = LoggerFactory.getLogger(PublisherClient.class);
    
    private final RestTemplate restTemplate;
    private final String publisherManagerUrl;

    public PublisherClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        // For Docker deployment: http://publisher-manager:8080
        // For local dev: http://localhost:8080
        this.publisherManagerUrl = System.getenv("PUBLISHER_MANAGER_URL") != null 
            ? System.getenv("PUBLISHER_MANAGER_URL") 
            : "http://localhost:8081/publisher";
    }

    /**
     * Fetch all publishers from the publisher-manager service
     */
    public List<PublisherDTO> getAllPublishers() {
        try {
            logger.info("Fetching publishers from: {}/all", publisherManagerUrl);
            PublisherDTO[] publishers = restTemplate.getForObject(
                publisherManagerUrl + "/all",
                PublisherDTO[].class
            );
            logger.info("Successfully fetched {} publishers", publishers != null ? publishers.length : 0);
            return publishers != null ? Arrays.asList(publishers) : List.of();
        } catch (RestClientException e) {
            logger.error("Failed to fetch publishers from publisher-manager service", e);
            throw new PublisherServiceException("Unable to fetch publishers from publisher-manager", e);
        }
    }

    /**
     * Check if a publisher exists by name
     */
    public boolean publisherExists(String publisherId) {
        try {
            logger.debug("Checking if publisher exists: {}", publisherId);
            List<PublisherDTO> publishers = getAllPublishers();
            boolean exists = publishers.stream()
                .anyMatch(p -> p.getName().equalsIgnoreCase(publisherId));
            logger.debug("Publisher '{}' exists: {}", publisherId, exists);
            return exists;
        } catch (Exception e) {
            logger.error("Error checking publisher existence for: {}", publisherId, e);
            throw new PublisherServiceException("Error validating publisher: " + publisherId, e);
        }
    }

    /**
     * Exception for publisher service errors
     */
    public static class PublisherServiceException extends RuntimeException {
        public PublisherServiceException(String message) {
            super(message);
        }

        public PublisherServiceException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
