package org.example.taskmanager.contracts.validation.period.range;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PeriodRangeValidator
        implements ConstraintValidator<ValidPeriodRange, PeriodRange> {

    @Override
    public boolean isValid(PeriodRange value, ConstraintValidatorContext context) {
        if(value == null) {
            return true;
        }

        if(value.periodStart() == null || value.periodEnd() == null) {
            return true;
        }

        return value.periodStart().isBefore(value.periodEnd());
    }
}
