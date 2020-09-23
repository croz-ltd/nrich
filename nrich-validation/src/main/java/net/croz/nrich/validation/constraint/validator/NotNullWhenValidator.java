package net.croz.nrich.validation.constraint.validator;

import net.croz.nrich.validation.api.constraint.NotNullWhen;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.function.Predicate;

public class NotNullWhenValidator extends BaseNullableCheckValidator implements ConstraintValidator<NotNullWhen, Object> {

    private String propertyName;

    private Class<? extends Predicate<?>> conditionClass;

    public NotNullWhenValidator(final AutowireCapableBeanFactory beanFactory) {
        super(beanFactory);
    }

    @Override
    public void initialize(final NotNullWhen constraintAnnotation) {
        propertyName = constraintAnnotation.property();
        conditionClass = constraintAnnotation.condition();
    }

    @Override
    public boolean isValid(final Object value, final ConstraintValidatorContext context) {
        return isValid(value, conditionClass, propertyName);
    }

    @Override
    protected boolean isPropertyValueValid(final Object propertyValue) {
        return propertyValue != null;
    }
}
