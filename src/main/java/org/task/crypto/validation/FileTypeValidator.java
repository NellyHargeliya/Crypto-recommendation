package org.task.crypto.validation;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;

public class FileTypeValidator implements ConstraintValidator<ValidFileType, MultipartFile> {

    private List<String> allowedTypes;

    @Override
    public void initialize(ValidFileType constraintAnnotation) {
        allowedTypes = Arrays.asList(constraintAnnotation.allowedTypes());
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (file == null || file.isEmpty()) {
            return false;
        }
        return allowedTypes.contains(file.getContentType());
    }
}
