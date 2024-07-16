package ru.kostrikov.gym_booking.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Path;

import java.util.*;
import java.util.stream.Stream;

public class ValidationException extends RuntimeException {

    private final Map<String, String> errors = new HashMap<>();

    public ValidationException(Set<? extends ConstraintViolation<?>> constraintViolations) {

        for (ConstraintViolation<?> violation : constraintViolations) {
            for (Path.Node node : violation.getPropertyPath()) {
                if (node.getName() != null) {
                    errors.put(node.getName(), violation.getMessage());
                    break;
                }
            }
        }
    }

    public Map<String, String> getErrors() {
        return Collections.unmodifiableMap(errors);
    }
}
