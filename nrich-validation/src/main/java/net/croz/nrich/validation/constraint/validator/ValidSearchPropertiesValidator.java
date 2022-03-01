package net.croz.nrich.validation.constraint.validator;

import net.croz.nrich.validation.api.constraint.ValidSearchProperties;
import net.croz.nrich.validation.constraint.util.ValidationReflectionUtil;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ValidSearchPropertiesValidator implements ConstraintValidator<ValidSearchProperties, Object> {

    private Map<String, String[]> propertyGroupMap;

    @Override
    public void initialize(ValidSearchProperties constraintAnnotation) {
        ValidSearchProperties.PropertyGroup[] propertyGroupList = constraintAnnotation.propertyGroup();

        propertyGroupMap = IntStream.range(0, propertyGroupList.length)
            .boxed()
            .collect(Collectors.toConcurrentMap(Object::toString, value -> propertyGroupList[value].value()));
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        Class<?> type = value.getClass();

        return propertyGroupMap.entrySet().stream().anyMatch(fieldGroup -> {
            List<Method> methodList = Arrays.stream(fieldGroup.getValue())
                .map(fieldName -> ValidationReflectionUtil.findGetterMethod(type, fieldName))
                .collect(Collectors.toList());

            return methodList.stream().allMatch(method -> ValidationReflectionUtil.invokeMethod(method, value) != null);
        });
    }
}
