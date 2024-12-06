package org.task.crypto.unit.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.task.crypto.validation.FileTypeValidator;
import org.task.crypto.validation.ValidFileType;

import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.Annotation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("unit")
class FileTypeValidatorTest {

    private FileTypeValidator fileTypeValidator;

    @Mock
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        fileTypeValidator = new FileTypeValidator();

        fileTypeValidator.initialize(new ValidFileType() {
            @Override
            public String message() {
                return "Invalid file type";
            }

            @Override
            public Class<?>[] groups() {
                return new Class[0];
            }

            @Override
            public Class<? extends Payload>[] payload() {
                return new Class[0];
            }

            @Override
            public String[] allowedTypes() {
                return new String[]{"text/csv"};
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return ValidFileType.class;
            }
        });
    }

    @Test
    void testIsValidWithValidFileType() {
        MockMultipartFile validFile = new MockMultipartFile(
                "file", "test.csv", "text/csv", "some,csv,data".getBytes());

        boolean isValid = fileTypeValidator.isValid(validFile, context);

        assertTrue(isValid, "File type should be valid");
    }

    @Test
    void testIsValidWithInvalidFileType() {
        MockMultipartFile invalidFile = new MockMultipartFile(
                "file", "test.docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "random content".getBytes());

        boolean isValid = fileTypeValidator.isValid(invalidFile, context);

        assertFalse(isValid, "File type should be invalid");
    }

    @Test
    void testIsValidWithEmptyFile() {
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file", "empty.csv", "text/csv", "".getBytes());

        boolean isValid = fileTypeValidator.isValid(emptyFile, context);

        assertFalse(isValid, "Empty file should be invalid");
    }

    @Test
    void testIsValidWithNullFile() {
        MockMultipartFile nullFile = null;

        boolean isValid = fileTypeValidator.isValid(nullFile, context);

        assertFalse(isValid, "Null file should be invalid");
    }

}