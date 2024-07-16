package ru.kostrikov.gym_booking.utils;

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


    public <T> Set<ConstraintViolation<T>> validate(T entity) {
        return getValidatorFactory().getValidator().validate(entity);
    }
}
