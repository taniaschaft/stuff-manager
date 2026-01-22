package com.gamemanager.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test suite for Game entity and its validators.
 * Tests include entity validation, hours validation, and constraint verification.
 */
public class GameModelTest {

    private Validator validator;

    @BeforeEach
    public void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // ==================== Game Entity Validation Tests ====================
    @Nested
    class GameEntityValidationTests {

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
            
            assertFalse(violations.isEmpty(), "Empty publisherId should fail validation");
            assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("publisherId cannot be empty")));
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
            
            assertFalse(violations.isEmpty(), "Blank publisherId should fail validation");
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
            
            assertFalse(violations.isEmpty(), "Name with less than 3 chars should fail");
            assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("name must contain from 3 up to 20 characters")));
        }

        @Test
        public void testGameWithNameTooLong_ShouldFailValidation() {
            Map<String, Integer> timePlayed = new HashMap<>();
            timePlayed.put("2023-05-01", 5);
            
            Game game = new Game();
            game.setId("test-id");
            game.setPublisherId("nintendo");
            game.setName("ThisIsAVeryLongGameNameThatExceedsLimit");
            game.setTimePlayed(timePlayed);
            
            Set<ConstraintViolation<Game>> violations = validator.validate(game);
            
            assertFalse(violations.isEmpty(), "Name exceeding 20 chars should fail");
        }

        @Test
        public void testGameWithEmptyTimePlayed_ShouldFailValidation() {
            Game game = new Game();
            game.setId("test-id");
            game.setPublisherId("nintendo");
            game.setName("Mario");
            game.setTimePlayed(new HashMap<>());
            
            Set<ConstraintViolation<Game>> violations = validator.validate(game);
            
            assertFalse(violations.isEmpty(), "Empty timePlayed should fail validation");
            assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("timePlayed cannot be empty")));
        }

        @Test
        public void testGameWithValidNameLength() {
            Map<String, Integer> timePlayed = new HashMap<>();
            timePlayed.put("2023-05-01", 5);
            
            Game game = new Game();
            game.setId("test-id");
            game.setPublisherId("nintendo");
            game.setName("Mario");  // Exactly 5 chars, valid
            game.setTimePlayed(timePlayed);
            
            Set<ConstraintViolation<Game>> violations = validator.validate(game);
            
            assertTrue(violations.isEmpty(), "Valid name length should pass");
        }

        @Test
        public void testGameWithBoundaryNameLength() {
            Map<String, Integer> timePlayed = new HashMap<>();
            timePlayed.put("2023-05-01", 5);
            
            Game game = new Game();
            game.setId("test-id");
            game.setPublisherId("nintendo");
            game.setName("ValidTwentyCharName");  // 19 chars, valid
            game.setTimePlayed(timePlayed);
            
            Set<ConstraintViolation<Game>> violations = validator.validate(game);
            
            assertTrue(violations.isEmpty(), "19-char name should pass");
        }

        @Test
        public void testGameWithMultipleDaysTimePlayed() {
            Map<String, Integer> timePlayed = new HashMap<>();
            timePlayed.put("2023-05-01", 5);
            timePlayed.put("2023-05-02", 8);
            timePlayed.put("2023-05-03", 10);
            
            Game game = new Game();
            game.setId("test-id");
            game.setPublisherId("nintendo");
            game.setName("Mario");
            game.setTimePlayed(timePlayed);
            
            Set<ConstraintViolation<Game>> violations = validator.validate(game);
            
            assertTrue(violations.isEmpty(), "Multiple valid days should pass");
        }
    }

    // ==================== GameHoursValidator Tests ====================
    @Nested
    @ExtendWith(MockitoExtension.class)
    class GameHoursValidatorTests {

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
            Map<String, Integer> timePlayed = new HashMap<>();
            timePlayed.put("2024-01-20", 5);
            timePlayed.put("2024-01-21", 10);
            timePlayed.put("2024-01-22", 24);
            
            assertTrue(validator.isValid(timePlayed, context));
        }

        @Test
        public void testHoursExceedingLimit() {
            Map<String, Integer> timePlayed = new HashMap<>();
            timePlayed.put("2024-01-20", 25);
            
            assertFalse(validator.isValid(timePlayed, context));
        }

        @Test
        public void testNegativeHours() {
            Map<String, Integer> timePlayed = new HashMap<>();
            timePlayed.put("2024-01-20", -5);
            
            assertFalse(validator.isValid(timePlayed, context));
        }

        @Test
        public void testZeroHours() {
            Map<String, Integer> timePlayed = new HashMap<>();
            timePlayed.put("2024-01-20", 0);
            
            assertTrue(validator.isValid(timePlayed, context));
        }

        @Test
        public void testMaxValidHours() {
            Map<String, Integer> timePlayed = new HashMap<>();
            timePlayed.put("2024-01-20", 24);
            
            assertTrue(validator.isValid(timePlayed, context));
        }

        @Test
        public void testOneHourOverLimit() {
            Map<String, Integer> timePlayed = new HashMap<>();
            timePlayed.put("2024-01-20", 25);
            
            assertFalse(validator.isValid(timePlayed, context));
        }

        @Test
        public void testMultipleDaysWithValidHours() {
            Map<String, Integer> timePlayed = new HashMap<>();
            timePlayed.put("2024-01-20", 5);
            timePlayed.put("2024-01-21", 8);
            timePlayed.put("2024-01-22", 12);
            timePlayed.put("2024-01-23", 24);
            
            assertTrue(validator.isValid(timePlayed, context));
        }

        @Test
        public void testMultipleDaysWithOneInvalid() {
            Map<String, Integer> timePlayed = new HashMap<>();
            timePlayed.put("2024-01-20", 5);
            timePlayed.put("2024-01-21", 8);
            timePlayed.put("2024-01-22", 25);  // Invalid
            timePlayed.put("2024-01-23", 24);
            
            assertFalse(validator.isValid(timePlayed, context));
        }

        @Test
        public void testNullMap() {
            assertTrue(validator.isValid(null, context));
        }

        @Test
        public void testEmptyMap() {
            Map<String, Integer> timePlayed = new HashMap<>();
            assertTrue(validator.isValid(timePlayed, context));
        }

        @Test
        public void testMapWithNullValue() {
            Map<String, Integer> timePlayed = new HashMap<>();
            timePlayed.put("2024-01-20", null);
            
            assertTrue(validator.isValid(timePlayed, context));
        }

        @Test
        public void testLargeNumberExceedsLimit() {
            Map<String, Integer> timePlayed = new HashMap<>();
            timePlayed.put("2024-01-20", 100);
            
            assertFalse(validator.isValid(timePlayed, context));
        }

        @Test
        public void testBoundaryAtOne() {
            Map<String, Integer> timePlayed = new HashMap<>();
            timePlayed.put("2024-01-20", 1);
            
            assertTrue(validator.isValid(timePlayed, context));
        }

        @Test
        public void testBoundaryAtTwentyThree() {
            Map<String, Integer> timePlayed = new HashMap<>();
            timePlayed.put("2024-01-20", 23);
            
            assertTrue(validator.isValid(timePlayed, context));
        }
    }
}
