package net.croz.nrich.validation.constraint.validator;

import lombok.RequiredArgsConstructor;
import net.croz.nrich.validation.constraint.util.ValidationReflectionUtil;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import java.lang.reflect.Method;
import java.util.function.Predicate;

@RequiredArgsConstructor
abstract class BaseNullableCheckValidator {

    private final AutowireCapableBeanFactory beanFactory;

    protected abstract boolean isPropertyValueValid(Object propertyValue);

    protected boolean isValid(final Object value, final Class<? extends Predicate<?>> conditionClass, final String propertyName) {
        if (value == null) {
            return true;
        }

        @SuppressWarnings("unchecked")
        final Predicate<Object> condition = (Predicate<Object>) beanFactory.autowire(conditionClass, AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR, false);

        final boolean conditionEvaluationResult = condition.test(value);

        if (!conditionEvaluationResult) {
            return true;
        }

        final Object propertyValue = resolvePropertyValue(value, propertyName);

        return isPropertyValueValid(propertyValue);
    }

    private Object resolvePropertyValue(final Object parent, final String propertyName) {
        final Method propertyGetterMethod = ValidationReflectionUtil.findGetterMethod(parent.getClass(), propertyName);

        if (propertyGetterMethod == null) {
            throw new IllegalArgumentException(String.format("No getter method found for property %s when invoking %s validator", propertyName, this.getClass().getSimpleName()));
        }

        return ValidationReflectionUtil.invokeMethod(propertyGetterMethod, parent);
    }
}
