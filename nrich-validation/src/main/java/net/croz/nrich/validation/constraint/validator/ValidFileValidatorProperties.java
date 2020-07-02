package net.croz.nrich.validation.constraint.validator;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ValidFileValidatorProperties {

    private Boolean validationEnabled;

    private List<String> allowedExtensionList;

    private List<String> allowedContentTypeList;

    private String allowedFileNameRegex;

}
