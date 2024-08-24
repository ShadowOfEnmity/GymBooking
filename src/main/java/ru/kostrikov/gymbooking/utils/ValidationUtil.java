package ru.kostrikov.gymbooking.utils;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Path;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@UtilityClass
public class ValidationUtil {

    private ValidatorFactory validatorFactory;

    public ValidatorFactory getValidatorFactory() {
        if (validatorFactory == null) {
            validatorFactory = Validation.buildDefaultValidatorFactory();
        }
        return validatorFactory;
    }

    public void closeValidatorFactory() {
        if (validatorFactory != null) {
            validatorFactory.close();
            validatorFactory = null;
        }
    }


    public Map<String, String> getMapOfViolationsToDisplay(Set<? extends ConstraintViolation<?>> constraintViolations) {
        Map<String, String> errors = new HashMap<>();
        for (ConstraintViolation<?> violation : constraintViolations) {
            for (Path.Node node : violation.getPropertyPath()) {
                if (node.getName() != null) {
                    errors.put(node.getName(), violation.getMessage());
                    break;
                }
            }
        }
        return errors;
    }

    public <T> Set<ConstraintViolation<T>> validate(T entity) {
        return getValidatorFactory().getValidator().validate(entity);
    }
}
