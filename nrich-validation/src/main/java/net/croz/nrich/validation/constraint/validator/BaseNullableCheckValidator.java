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

    protected boolean isValid(Object value, Class<? extends Predicate<?>> conditionClass, String propertyName) {
        if (value == null) {
            return true;
        }

        @SuppressWarnings("unchecked")
        Predicate<Object> condition = (Predicate<Object>) beanFactory.autowire(conditionClass, AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR, false);

        boolean conditionEvaluationResult = condition.test(value);

        if (!conditionEvaluationResult) {
            return true;
        }

        Object propertyValue = resolvePropertyValue(value, propertyName);

        return isPropertyValueValid(propertyValue);
    }

    private Object resolvePropertyValue(Object parent, String propertyName) {
        Method propertyGetterMethod = ValidationReflectionUtil.findGetterMethod(parent.getClass(), propertyName);

        if (propertyGetterMethod == null) {
            throw new IllegalArgumentException(String.format("No getter method found for property %s when invoking %s validator", propertyName, this.getClass().getSimpleName()));
        }

        return ValidationReflectionUtil.invokeMethod(propertyGetterMethod, parent);
    }
}
