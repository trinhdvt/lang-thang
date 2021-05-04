package com.langthang.annotation.validator;

import com.langthang.annotation.ValidImage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ImageValidator implements ConstraintValidator<ValidImage, MultipartFile> {

    @Value("${application.image.support-type}")
    private String[] supportedTypes;

    @Override
    public void initialize(ValidImage constraintAnnotation) {
    }

    @Override
    public boolean isValid(MultipartFile multipartFile, ConstraintValidatorContext context) {
        String contentType = multipartFile.getContentType();
        if (!isSupportedContentType(contentType)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Not support type")
                    .addConstraintViolation();
            return false;
        }
        return true;
    }

    private boolean isSupportedContentType(String contentType) {
        for (String type : supportedTypes) {
            if (type.equals(contentType))
                return true;
        }
        return false;
    }
}
