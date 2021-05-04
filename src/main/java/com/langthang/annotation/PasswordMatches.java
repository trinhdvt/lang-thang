package com.langthang.annotation;

import com.langthang.annotation.validator.PasswordMatchesValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = PasswordMatchesValidator.class)
public @interface PasswordMatches {
    String message() default "Password don't match";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
