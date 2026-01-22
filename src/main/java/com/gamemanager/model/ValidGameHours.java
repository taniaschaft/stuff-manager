package com.gamemanager.model;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = GameHoursValidator.class)
@Documented
public @interface ValidGameHours {
    String message() default "hours_played cannot exceed 24 hours per day";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
