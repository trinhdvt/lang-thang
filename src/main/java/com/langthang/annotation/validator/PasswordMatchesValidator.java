package com.langthang.annotation.validator;

import com.langthang.annotation.PasswordMatches;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

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
            Field passwordField = clazz.getDeclaredField("password");
            Field passMatchesField = clazz.getDeclaredField("passwordConfirm");

            passwordField.setAccessible(true);
            passMatchesField.setAccessible(true);

            return passwordField.get(object).equals(passMatchesField.get(object));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return false;
        }
    }
}
