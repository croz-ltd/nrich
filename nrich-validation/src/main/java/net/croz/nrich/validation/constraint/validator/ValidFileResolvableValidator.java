package net.croz.nrich.validation.constraint.validator;

import net.croz.nrich.validation.api.constraint.ValidFileResolvable;
import org.springframework.core.env.Environment;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Optional;

public class ValidFileResolvableValidator extends BaseValidFileValidator implements ConstraintValidator<ValidFileResolvable, Object> {

    private final Environment environment;

    public ValidFileResolvableValidator(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void initialize(ValidFileResolvable constraintAnnotation) {
        this.allowedContentTypeList = resolvePropertyValue(constraintAnnotation.allowedContentTypeListPropertyName(), String[].class, new String[0]);
        this.allowedExtensionList = resolvePropertyValue(constraintAnnotation.allowedExtensionListPropertyName(), String[].class, new String[0]);
        this.allowedFileNameRegex = resolvePropertyValue(constraintAnnotation.allowedFileNameRegexPropertyName(), String.class, "");
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return isValid(value);
    }

    private <T> T resolvePropertyValue(String propertyName, Class<T> propertyType, T defaultPropertyValue) {
        if (!propertyName.isEmpty()) {
            return Optional.ofNullable(environment.getProperty(propertyName, propertyType)).orElse(defaultPropertyValue);
        }

        return defaultPropertyValue;
    }
}
