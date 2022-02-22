package net.croz.nrich.validation.constraint.validator;

import net.croz.nrich.validation.api.constraint.InList;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;

public class InListValidator implements ConstraintValidator<InList, Object> {

    private String[] stringList;

    @Override
    public void initialize(InList constraintAnnotation) {
        stringList = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        return Arrays.asList(stringList).contains(value.toString());
    }
}
