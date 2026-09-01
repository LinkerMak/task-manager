package org.example.taskmanager.contracts.summary.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.example.taskmanager.contracts.summary.TaskSummaryRequest;

public class SummaryPeriodValidator
        implements ConstraintValidator<ValidSummaryPeriod, TaskSummaryRequest> {
    @Override
    public boolean isValid(TaskSummaryRequest request, ConstraintValidatorContext context) {
        if(request == null) {
            return true;
        }

        if(request.periodStart() == null || request.periodEnd() == null) {
            return true;
        }

        return request.periodStart().isBefore(request.periodEnd());
    }
}
