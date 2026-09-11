package org.example.scheduler.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ContractValidator {

    private final Validator validator;

    public <T> Set<ConstraintViolation<T>> validate(T value) {
        return validator.validate(value);
    }

    public String formatViolations(
            Set<? extends ConstraintViolation<?>> violations
    ) {
        return violations.stream()
                .map(violation -> {
                    String propertyPath = violation.getPropertyPath().toString();

                    if(propertyPath.isBlank()) {
                        return violation.getMessage();
                    }

                    return "%s: %s".formatted(
                            propertyPath,
                            violation.getMessage()
                    );

                })
                .sorted()
                .collect(Collectors.joining(", "));
    }
}
