package com.gamemanager.controller;

import com.gamemanager.model.Game;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * Integration test for GameController validation.
 * Tests that Jakarta Bean Validation constraints are properly enforced.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class GameControllerValidationIT {

    @Autowired
    private MockMvc mockMvc;

    /**
     * Test that POST /game with empty publisherId returns 400 Bad Request
     * and includes validation error message.
     */
    @Test
    public void testCreateGameWithEmptyPublisherId_ShouldReturn400() throws Exception {
        // Prepare invalid game payload with empty publisherId
        String invalidGameJson = """
        {
            "id": "test-game-id-001",
            "publisherId": "",
            "name": "TestGame",
            "timePlayed": {
                "2023-05-01": 5
            }
        }
        """;

        // Execute POST /game with invalid payload
        mockMvc.perform(post("/game")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidGameJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").exists());
    }

    /**
     * Test that POST /game with blank publisherId (whitespace) returns 400 Bad Request.
     */
    @Test
    public void testCreateGameWithBlankPublisherId_ShouldReturn400() throws Exception {
        // Prepare invalid game payload with blank publisherId
        String invalidGameJson = """
        {
            "id": "test-game-id-002",
            "publisherId": "   ",
            "name": "TestGame",
            "timePlayed": {
                "2023-05-01": 5
            }
        }
        """;

        // Execute POST /game with invalid payload
        mockMvc.perform(post("/game")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidGameJson))
                .andExpect(status().isBadRequest());
    }

    /**
     * Test that POST /game with empty name returns 400 Bad Request.
     */
    @Test
    public void testCreateGameWithEmptyName_ShouldReturn400() throws Exception {
        // Prepare invalid game payload with empty name
        String invalidGameJson = """
        {
            "id": "test-game-id-003",
            "publisherId": "nintendo",
            "name": "",
            "timePlayed": {
                "2023-05-01": 5
            }
        }
        """;

        // Execute POST /game with invalid payload
        mockMvc.perform(post("/game")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidGameJson))
                .andExpect(status().isBadRequest());
    }

    /**
     * Test that POST /game with name too short (less than 3 characters) returns 400 Bad Request.
     */
    @Test
    public void testCreateGameWithNameTooShort_ShouldReturn400() throws Exception {
        // Prepare invalid game payload with name too short
        String invalidGameJson = """
        {
            "id": "test-game-id-004",
            "publisherId": "nintendo",
            "name": "ab",
            "timePlayed": {
                "2023-05-01": 5
            }
        }
        """;

        // Execute POST /game with invalid payload
        mockMvc.perform(post("/game")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidGameJson))
                .andExpect(status().isBadRequest());
    }

    /**
     * Test that POST /game with empty timePlayed returns 400 Bad Request.
     */
    @Test
    public void testCreateGameWithEmptyTimePlayed_ShouldReturn400() throws Exception {
        // Prepare invalid game payload with empty timePlayed
        String invalidGameJson = """
        {
            "id": "test-game-id-005",
            "publisherId": "nintendo",
            "name": "Mario",
            "timePlayed": {}
        }
        """;

        // Execute POST /game with invalid payload
        mockMvc.perform(post("/game")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidGameJson))
                .andExpect(status().isBadRequest());
    }
}
