package net.croz.nrich.validation.constraint.validator;

import net.croz.nrich.validation.api.constraint.ValidOib;
import net.croz.nrich.validation.constraint.util.OibValidatorUtil;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidOibValidator implements ConstraintValidator<ValidOib, String> {

    @Override
    public boolean isValid(final String value, final ConstraintValidatorContext context) {
        // will be validated by other constraints
        if (value == null || value.isEmpty()) {
            return true;
        }

        return OibValidatorUtil.validOib(value);
    }
}
