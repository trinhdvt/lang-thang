package com.langthang.annotation.validator;

import com.langthang.annotation.PasswordMatches;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {

    @Override
    public void initialize(PasswordMatches constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext context) {

        try {
            Class<?> clazz = object.getClass();
            Field passwordField = clazz.getField("password");
            Field passMatchesField = clazz.getField("matchedPassword");

            return passwordField.get(object).equals(passMatchesField.getLong(object));

        } catch (NoSuchFieldException | IllegalAccessException e) {
            return false;
        }
    }
}
