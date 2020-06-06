package net.croz.nrich.validation.constraint.validator;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class ValidFileValidatorProperties {

    private final Boolean validationEnabled;

    private final List<String> allowedExtensionList;

    private final List<String> allowedContentTypeList;

    private final String allowedFileNameRegex;

}
