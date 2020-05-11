package net.croz.nrich.validation.constraint.validator;

import lombok.RequiredArgsConstructor;
import net.croz.nrich.validation.api.constraint.ValidFile;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
public class ValidFileValidator implements ConstraintValidator<ValidFile, Object> {

    private final ValidFileValidatorProperties validFileValidatorProperties;

    @Override
    public boolean isValid(final Object value, final ConstraintValidatorContext context) {
        if (Boolean.FALSE.equals(validFileValidatorProperties.getValidationEnabled()) || value == null) {
            return true;
        }

        final String fileName;
        final String fileContentType;
        if (value instanceof MultipartFile) {
            fileName = extractFileName(((MultipartFile) value).getName());
            fileContentType = ((MultipartFile) value).getContentType();
        }
        else if (value instanceof FilePart) {
            fileName = extractFileName(((FilePart) value).filename());
            fileContentType = Optional.ofNullable(((FilePart) value).headers().getContentType()).map(Objects::toString).orElse(null);
        }
        else {
            throw new IllegalArgumentException(String.format("Unable to validate file, unrecognized type: %s", value.getClass()));
        }

        boolean valid = true;
        if (fileContentType != null && validFileValidatorProperties.getAllowedContentTypeList() != null) {
            valid = validFileValidatorProperties.getAllowedContentTypeList().contains(fileContentType);
        }
        if (validFileValidatorProperties.getAllowedFileNameRegex() != null) {
            valid &= fileName.matches(validFileValidatorProperties.getAllowedFileNameRegex());
        }
        if (validFileValidatorProperties.getAllowedExtensionList() != null) {
            final String[] fileNameList = fileName.split("\\.");

            final String extension;
            if (fileNameList.length > 1) {
                extension = fileNameList[fileNameList.length - 1];
            }
            else {
                extension = null;
            }

            if (extension != null) {
                valid &= validFileValidatorProperties.getAllowedExtensionList().stream().anyMatch(extension::equalsIgnoreCase);
            }
        }

        return valid;
    }

    private String extractFileName(final String fileName) {
        if (fileName == null) {
            return "";
        }
        // Check for Unix-style path
        int pos = fileName.lastIndexOf('/');
        if (pos == -1) {
            // Check for Windows-style path
            pos = fileName.lastIndexOf('\\');
        }
        if (pos != -1) {
            // Any sort of path separator found...
            return fileName.substring(pos + 1);
        }

        return fileName;
    }
}
