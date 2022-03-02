package net.croz.nrich.validation.constraint.validator;

import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

abstract class BaseValidFileValidator {

    protected String[] allowedContentTypeList;

    protected String[] allowedExtensionList;

    protected String allowedFileNameRegex;

    protected boolean isValid(Object value) {
        if (value == null) {
            return true;
        }

        String fileName;
        String fileContentType;
        if (value instanceof MultipartFile) {
            fileName = extractFileName(((MultipartFile) value).getName());
            fileContentType = ((MultipartFile) value).getContentType();
        }
        else if (value instanceof FilePart) {
            fileName = extractFileName(((FilePart) value).filename());
            fileContentType = Optional.ofNullable(((FilePart) value).headers().getContentType())
                .map(Objects::toString)
                .orElse(null);
        }
        else {
            throw new IllegalArgumentException(String.format("Unable to validate file, unrecognized type: %s", value.getClass()));
        }

        boolean valid = true;
        if (fileContentType != null && allowedContentTypeList.length > 0) {
            valid = Arrays.asList(allowedContentTypeList).contains(fileContentType);
        }
        if (!allowedFileNameRegex.isEmpty()) {
            valid &= fileName.matches(allowedFileNameRegex);
        }
        if (allowedExtensionList.length > 0) {
            String[] fileNameList = fileName.split("\\.");

            String extension;
            if (fileNameList.length > 1) {
                extension = fileNameList[fileNameList.length - 1];
            }
            else {
                extension = null;
            }

            if (extension != null) {
                valid &= Arrays.stream(allowedExtensionList).anyMatch(extension::equalsIgnoreCase);
            }
        }

        return valid;
    }

    private String extractFileName(String fileName) {
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
