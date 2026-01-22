package com.gamemanager.model;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Map;

public class GameHoursValidator implements ConstraintValidator<ValidGameHours, Map<String, Integer>> {
    @Override
    public void initialize(ValidGameHours annotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(Map<String, Integer> value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Let @NotEmpty handle null/empty case
        }

        for (Integer hours : value.values()) {
            if (hours != null && (hours < 0 || hours > 24)) {
                return false;
            }
        }
        return true;
    }
}
