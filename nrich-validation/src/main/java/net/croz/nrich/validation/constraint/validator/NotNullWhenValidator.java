package net.croz.nrich.validation.constraint.validator;

import net.croz.nrich.validation.api.constraint.NotNullWhen;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.function.Predicate;

public class NotNullWhenValidator extends BaseNullableCheckValidator implements ConstraintValidator<NotNullWhen, Object> {

    private String propertyName;

    private Class<? extends Predicate<?>> conditionClass;

    public NotNullWhenValidator(AutowireCapableBeanFactory beanFactory) {
        super(beanFactory);
    }

    @Override
    public void initialize(NotNullWhen constraintAnnotation) {
        propertyName = constraintAnnotation.property();
        conditionClass = constraintAnnotation.condition();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return isValid(value, conditionClass, propertyName);
    }

    @Override
    protected boolean isPropertyValueValid(Object propertyValue) {
        return propertyValue != null;
    }
}
