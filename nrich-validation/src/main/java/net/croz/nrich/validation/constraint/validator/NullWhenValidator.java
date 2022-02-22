package net.croz.nrich.validation.constraint.validator;

import net.croz.nrich.validation.api.constraint.NullWhen;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.function.Predicate;

public class NullWhenValidator extends BaseNullableCheckValidator implements ConstraintValidator<NullWhen, Object> {

    private String propertyName;

    private Class<? extends Predicate<?>> conditionClass;

    public NullWhenValidator(AutowireCapableBeanFactory beanFactory) {
        super(beanFactory);
    }

    @Override
    public void initialize(NullWhen constraintAnnotation) {
        propertyName = constraintAnnotation.property();
        conditionClass = constraintAnnotation.condition();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return isValid(value, conditionClass, propertyName);
    }

    @Override
    protected boolean isPropertyValueValid(Object propertyValue) {
        return propertyValue == null;
    }
}
