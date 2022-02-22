package net.croz.nrich.validation.constraint.validator;

import net.croz.nrich.validation.api.constraint.MaxSizeInBytes;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.nio.charset.Charset;

public class MaxSizeInBytesValidator implements ConstraintValidator<MaxSizeInBytes, String> {

    private int maxSizeInBytes;

    private Charset charset;

    @Override
    public void initialize(MaxSizeInBytes constraintAnnotation) {
        maxSizeInBytes = constraintAnnotation.value();
        charset = Charset.forName(constraintAnnotation.encoding());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        return value.getBytes(charset).length <= maxSizeInBytes;
    }
}
