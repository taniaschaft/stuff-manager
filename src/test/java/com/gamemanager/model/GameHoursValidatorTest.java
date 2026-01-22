package com.gamemanager.model;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class GameHoursValidatorTest {

    @Mock
    private ConstraintValidatorContext context;

    private GameHoursValidator validator;

    @BeforeEach
    public void setUp() {
        validator = new GameHoursValidator();
        validator.initialize(null);
    }

    @Test
    public void testValidHours() {
        // Arrange
        Map<String, Integer> timePlayed = new HashMap<>();
        timePlayed.put("2024-01-20", 5);
        timePlayed.put("2024-01-21", 10);
        timePlayed.put("2024-01-22", 24);
        
        // Act & Assert
        assertTrue(validator.isValid(timePlayed, context));
    }

    @Test
    public void testHoursExceedingLimit() {
        // Arrange
        Map<String, Integer> timePlayed = new HashMap<>();
        timePlayed.put("2024-01-20", 25);
        
        // Act & Assert
        assertFalse(validator.isValid(timePlayed, context));
    }

    @Test
    public void testNegativeHours() {
        // Arrange
        Map<String, Integer> timePlayed = new HashMap<>();
        timePlayed.put("2024-01-20", -5);
        
        // Act & Assert
        assertFalse(validator.isValid(timePlayed, context));
    }

    @Test
    public void testZeroHours() {
        // Arrange
        Map<String, Integer> timePlayed = new HashMap<>();
        timePlayed.put("2024-01-20", 0);
        
        // Act & Assert
        assertTrue(validator.isValid(timePlayed, context));
    }

    @Test
    public void testMaxValidHours() {
        // Arrange
        Map<String, Integer> timePlayed = new HashMap<>();
        timePlayed.put("2024-01-20", 24);
        
        // Act & Assert
        assertTrue(validator.isValid(timePlayed, context));
    }

    @Test
    public void testOneHourOverLimit() {
        // Arrange
        Map<String, Integer> timePlayed = new HashMap<>();
        timePlayed.put("2024-01-20", 25);
        
        // Act & Assert
        assertFalse(validator.isValid(timePlayed, context));
    }

    @Test
    public void testMultipleDaysWithValidHours() {
        // Arrange
        Map<String, Integer> timePlayed = new HashMap<>();
        timePlayed.put("2024-01-20", 5);
        timePlayed.put("2024-01-21", 8);
        timePlayed.put("2024-01-22", 12);
        timePlayed.put("2024-01-23", 24);
        
        // Act & Assert
        assertTrue(validator.isValid(timePlayed, context));
    }

    @Test
    public void testMultipleDaysWithOneInvalid() {
        // Arrange
        Map<String, Integer> timePlayed = new HashMap<>();
        timePlayed.put("2024-01-20", 5);
        timePlayed.put("2024-01-21", 8);
        timePlayed.put("2024-01-22", 25);  // Invalid
        timePlayed.put("2024-01-23", 24);
        
        // Act & Assert
        assertFalse(validator.isValid(timePlayed, context));
    }

    @Test
    public void testNullMap() {
        // Act & Assert
        assertTrue(validator.isValid(null, context));
    }

    @Test
    public void testEmptyMap() {
        // Arrange
        Map<String, Integer> timePlayed = new HashMap<>();
        
        // Act & Assert - Empty map is valid for this validator
        // @NotEmpty annotation handles the empty case
        assertTrue(validator.isValid(timePlayed, context));
    }

    @Test
    public void testMapWithNullValue() {
        // Arrange
        Map<String, Integer> timePlayed = new HashMap<>();
        timePlayed.put("2024-01-20", null);
        
        // Act & Assert - null values are skipped
        assertTrue(validator.isValid(timePlayed, context));
    }

    @Test
    public void testLargeNumberExceedsLimit() {
        // Arrange
        Map<String, Integer> timePlayed = new HashMap<>();
        timePlayed.put("2024-01-20", 100);
        
        // Act & Assert
        assertFalse(validator.isValid(timePlayed, context));
    }

    @Test
    public void testBoundaryAtOne() {
        // Arrange
        Map<String, Integer> timePlayed = new HashMap<>();
        timePlayed.put("2024-01-20", 1);
        
        // Act & Assert
        assertTrue(validator.isValid(timePlayed, context));
    }

    @Test
    public void testBoundaryAtTwentyThree() {
        // Arrange
        Map<String, Integer> timePlayed = new HashMap<>();
        timePlayed.put("2024-01-20", 23);
        
        // Act & Assert
        assertTrue(validator.isValid(timePlayed, context));
    }
}
