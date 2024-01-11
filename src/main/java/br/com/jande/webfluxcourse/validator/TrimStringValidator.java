package br.com.jande.webfluxcourse.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Objects;

public class TrimStringValidator implements ConstraintValidator<TrimString, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(Objects.isNull(value))
            return true;

        return value.trim().length() == value.length();
    }
}
