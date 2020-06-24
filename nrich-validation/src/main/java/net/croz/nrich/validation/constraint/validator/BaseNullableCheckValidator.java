package net.croz.nrich.validation.constraint.validator;

import net.croz.nrich.validation.constraint.util.ValidationReflectionUtil;

import java.lang.reflect.Method;
import java.util.function.Predicate;

abstract class BaseNullableCheckValidator {

    protected abstract boolean isPropertyValueValid(Object propertyValue);

    protected boolean isValid(final Object value, final Class<? extends Predicate<?>> conditionClass, final String propertyName) {
        if (value == null) {
            return true;
        }

        @SuppressWarnings("unchecked")
        final Predicate<Object> condition = (Predicate<Object>) ValidationReflectionUtil.instantiateClass(conditionClass);

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
