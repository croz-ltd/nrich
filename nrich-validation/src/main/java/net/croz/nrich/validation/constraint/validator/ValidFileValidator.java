package net.croz.nrich.validation.constraint.validator;

import net.croz.nrich.validation.api.constraint.ValidFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidFileValidator extends BaseValidFileValidator implements ConstraintValidator<ValidFile, Object> {

    @Override
    public void initialize(ValidFile constraintAnnotation) {
        this.allowedContentTypeList = constraintAnnotation.allowedContentTypeList();
        this.allowedExtensionList = constraintAnnotation.allowedExtensionList();
        this.allowedFileNameRegex = constraintAnnotation.allowedFileNameRegex();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return isValid(value);
    }
}
