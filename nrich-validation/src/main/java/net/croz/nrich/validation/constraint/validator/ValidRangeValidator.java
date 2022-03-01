package net.croz.nrich.validation.constraint.validator;

import net.croz.nrich.validation.api.constraint.ValidRange;
import net.croz.nrich.validation.constraint.util.ValidationReflectionUtil;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Method;

public class ValidRangeValidator implements ConstraintValidator<ValidRange, Object> {

    private String fromPropertyName;

    private String toPropertyName;

    private boolean inclusive;

    @Override
    public void initialize(ValidRange constraintAnnotation) {
        fromPropertyName = constraintAnnotation.fromPropertyName();
        toPropertyName = constraintAnnotation.toPropertyName();
        inclusive = constraintAnnotation.inclusive();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        Class<?> type = value.getClass();
        Method fromFieldGetter = ValidationReflectionUtil.findGetterMethod(type, fromPropertyName);
        Method toFieldGetter = ValidationReflectionUtil.findGetterMethod(type, toPropertyName);

        Object fromFieldValue = ValidationReflectionUtil.invokeMethod(fromFieldGetter, value);
        Object toFieldValue = ValidationReflectionUtil.invokeMethod(toFieldGetter, value);

        if (fromFieldValue == null || toFieldValue == null) {
            return true;
        }

        if (!(fromFieldValue instanceof Comparable<?> && toFieldValue instanceof Comparable<?>) || !fromFieldValue.getClass().equals(toFieldValue.getClass())) {
            throw new IllegalArgumentException("Both to and from fields have to be instances of comparable and of same type");
        }

        @SuppressWarnings("unchecked")
        int compareResult = ((Comparable<Object>) fromFieldValue).compareTo(toFieldValue);

        return compareResult < 0 || inclusive && compareResult == 0;
    }
}
