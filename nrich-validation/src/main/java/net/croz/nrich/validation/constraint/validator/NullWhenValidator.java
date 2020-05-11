package net.croz.nrich.validation.constraint.validator;

import net.croz.nrich.validation.api.constraint.NullWhen;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.function.Predicate;

public class NullWhenValidator extends BaseNullableCheckValidator implements ConstraintValidator<NullWhen, Object> {

    private String propertyName;

    private Class<? extends Predicate<?>> conditionClass;

    @Override
    public void initialize(final NullWhen constraintAnnotation) {
        propertyName = constraintAnnotation.property();
        conditionClass = constraintAnnotation.condition();
    }

    @Override
    public boolean isValid(final Object value, final ConstraintValidatorContext context) {
        return isValid(value, conditionClass, propertyName);
    }

    @Override
    protected boolean isPropertyValueValid(final Object propertyValue) {
        return propertyValue == null;
    }
}
