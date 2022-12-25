package com.langthang.annotation.validator;

import com.langthang.annotation.ValidImage;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;


public class ImageValidator implements ConstraintValidator<ValidImage, MultipartFile> {

    @Value("${application.image.support-type}")
    private String[] supportedTypes;

    @Override
    public void initialize(ValidImage constraintAnnotation) {
    }

    @Override
    public boolean isValid(MultipartFile multipartFile, ConstraintValidatorContext context) {
        String contentType = multipartFile.getContentType();
        return isSupportedContentType(contentType);
    }

    private boolean isSupportedContentType(String contentType) {
        for (String type : supportedTypes) {
            if (type.equals(contentType))
                return true;
        }
        return false;
    }
}
