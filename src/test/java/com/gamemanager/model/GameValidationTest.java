package com.gamemanager.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for Game entity validation.
 * Tests that Jakarta Bean Validation constraints are defined correctly.
 */
public class GameValidationTest {

    private Validator validator;

    @BeforeEach
    public void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testValidGame_ShouldPassValidation() {
        Map<String, Integer> timePlayed = new HashMap<>();
        timePlayed.put("2023-05-01", 5);
        
        Game game = new Game();
        game.setId("test-id");
        game.setPublisherId("nintendo");
        game.setName("Mario");
        game.setTimePlayed(timePlayed);
        
        Set<ConstraintViolation<Game>> violations = validator.validate(game);
        
        assertTrue(violations.isEmpty(), "Valid game should have no validation errors");
    }

    @Test
    public void testGameWithEmptyPublisherId_ShouldFailValidation() {
        Map<String, Integer> timePlayed = new HashMap<>();
        timePlayed.put("2023-05-01", 5);
        
        Game game = new Game();
        game.setId("test-id");
        game.setPublisherId("");
        game.setName("Mario");
        game.setTimePlayed(timePlayed);
        
        Set<ConstraintViolation<Game>> violations = validator.validate(game);
        
        assertFalse(violations.isEmpty(), "Game with empty publisherId should have validation errors");
        assertEquals(1, violations.size());
        
        ConstraintViolation<Game> violation = violations.iterator().next();
        assertEquals("publisherId", violation.getPropertyPath().toString());
        assertEquals("publisherId cannot be empty", violation.getMessage());
    }

    @Test
    public void testGameWithBlankPublisherId_ShouldFailValidation() {
        Map<String, Integer> timePlayed = new HashMap<>();
        timePlayed.put("2023-05-01", 5);
        
        Game game = new Game();
        game.setId("test-id");
        game.setPublisherId("   ");
        game.setName("Mario");
        game.setTimePlayed(timePlayed);
        
        Set<ConstraintViolation<Game>> violations = validator.validate(game);
        
        assertFalse(violations.isEmpty(), "Game with blank publisherId should have validation errors");
    }

    @Test
    public void testGameWithNameTooShort_ShouldFailValidation() {
        Map<String, Integer> timePlayed = new HashMap<>();
        timePlayed.put("2023-05-01", 5);
        
        Game game = new Game();
        game.setId("test-id");
        game.setPublisherId("nintendo");
        game.setName("ab");
        game.setTimePlayed(timePlayed);
        
        Set<ConstraintViolation<Game>> violations = validator.validate(game);
        
        assertFalse(violations.isEmpty(), "Game with name too short should have validation errors");
        
        boolean foundSizeViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("name"));
        assertTrue(foundSizeViolation, "Should have validation error for name field");
    }

    @Test
    public void testGameWithEmptyTimePlayed_ShouldFailValidation() {
        Map<String, Integer> timePlayed = new HashMap<>();
        
        Game game = new Game();
        game.setId("test-id");
        game.setPublisherId("nintendo");
        game.setName("Mario");
        game.setTimePlayed(timePlayed);
        
        Set<ConstraintViolation<Game>> violations = validator.validate(game);
        
        assertFalse(violations.isEmpty(), "Game with empty timePlayed should have validation errors");
        
        boolean foundTimePlayedViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("timePlayed"));
        assertTrue(foundTimePlayedViolation, "Should have validation error for timePlayed field");
    }
}
