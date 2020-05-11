package net.croz.nrich.validation.constraint.validator;

import lombok.Data;

import java.util.List;

@Data
public class ValidFileValidatorProperties {

    private final Boolean validationEnabled;

    private final List<String> allowedExtensionList;

    private final List<String> allowedContentTypeList;

    private final String allowedFileNameRegex;

}
