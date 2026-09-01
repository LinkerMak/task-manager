package org.example.taskmanager.contracts.summary.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Target(TYPE)
@Retention(RUNTIME)
@Constraint(validatedBy = SummaryPeriodValidator.class)
public @interface ValidSummaryPeriod {

    String message() default "periodStart must be before periodEnd";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
