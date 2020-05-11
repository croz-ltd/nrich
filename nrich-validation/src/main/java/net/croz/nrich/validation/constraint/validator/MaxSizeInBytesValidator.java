package net.croz.nrich.validation.constraint.validator;

import net.croz.nrich.validation.api.constraint.MaxSizeInBytes;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.nio.charset.StandardCharsets;

public class MaxSizeInBytesValidator implements ConstraintValidator<MaxSizeInBytes, String> {

    private long maxSizeInBytes;

    @Override
    public void initialize(final MaxSizeInBytes constraintAnnotation) {
        maxSizeInBytes = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(final String value, final ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        return value.getBytes(StandardCharsets.UTF_8).length <= maxSizeInBytes;
    }
}
