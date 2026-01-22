package com.gamemanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class GameControllerValidationIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCreateGame_WithEmptyPublisherId_ShouldReturn400() throws Exception {
        // Arrange - Create a game with empty publisherId
        Map<String, Object> gamePayload = new HashMap<>();
        gamePayload.put("id", "test-id-123");
        gamePayload.put("publisherId", "");  // Empty publisherId should fail validation
        gamePayload.put("name", "TestGame");
        Map<String, Integer> timePlayed = new HashMap<>();
        timePlayed.put("2023-05-01", 10);
        gamePayload.put("timePlayed", timePlayed);

        String jsonPayload = objectMapper.writeValueAsString(gamePayload);

        // Act & Assert - POST request should return 400 Bad Request
        mockMvc.perform(post("/game")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonPayload))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateGame_WithNullPublisherId_ShouldReturn400() throws Exception {
        // Arrange - Create a game with null publisherId
        Map<String, Object> gamePayload = new HashMap<>();
        gamePayload.put("id", "test-id-456");
        gamePayload.put("publisherId", null);  // Null publisherId should fail validation
        gamePayload.put("name", "TestGame");
        Map<String, Integer> timePlayed = new HashMap<>();
        timePlayed.put("2023-05-01", 10);
        gamePayload.put("timePlayed", timePlayed);

        String jsonPayload = objectMapper.writeValueAsString(gamePayload);

        // Act & Assert - POST request should return 400 Bad Request
        mockMvc.perform(post("/game")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonPayload))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateGame_WithInvalidName_ShouldReturn400() throws Exception {
        // Arrange - Create a game with name that's too short
        Map<String, Object> gamePayload = new HashMap<>();
        gamePayload.put("id", "test-id-789");
        gamePayload.put("publisherId", "nintendo");
        gamePayload.put("name", "ab");  // Name too short (< 3 characters)
        Map<String, Integer> timePlayed = new HashMap<>();
        timePlayed.put("2023-05-01", 10);
        gamePayload.put("timePlayed", timePlayed);

        String jsonPayload = objectMapper.writeValueAsString(gamePayload);

        // Act & Assert - POST request should return 400 Bad Request
        mockMvc.perform(post("/game")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonPayload))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateGame_WithEmptyTimePlayed_ShouldReturn400() throws Exception {
        // Arrange - Create a game with empty timePlayed
        Map<String, Object> gamePayload = new HashMap<>();
        gamePayload.put("id", "test-id-999");
        gamePayload.put("publisherId", "nintendo");
        gamePayload.put("name", "Mario");
        gamePayload.put("timePlayed", new HashMap<String, Integer>());  // Empty map should fail validation

        String jsonPayload = objectMapper.writeValueAsString(gamePayload);

        // Act & Assert - POST request should return 400 Bad Request
        mockMvc.perform(post("/game")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonPayload))
                .andExpect(status().isBadRequest());
    }
}
