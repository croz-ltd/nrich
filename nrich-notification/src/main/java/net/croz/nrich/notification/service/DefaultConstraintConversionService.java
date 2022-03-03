package net.croz.nrich.notification.service;

import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.beanvalidation.CustomValidatorBean;

import javax.validation.ConstraintViolation;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class DefaultConstraintConversionService implements ConstraintConversionService {

    private final ValidatorConverter validatorConverter = new ValidatorConverter();

    @Override
    public Object resolveTarget(Set<ConstraintViolation<?>> constraintViolationList) {
        return constraintViolationList.stream()
            .map(ConstraintViolation::getLeafBean)
            .filter(Objects::nonNull)
            .findFirst()
            .orElse(null);
    }

    @Override
    public Errors convertConstraintViolationsToErrors(Set<ConstraintViolation<?>> constraintViolationList, Object target, String targetName) {
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(target, targetName);

        Set<ConstraintViolation<Object>> convertedConstraintViolationList = constraintViolationList.stream().map(this::asObjectConstraintViolation).collect(Collectors.toSet());

        validatorConverter.processConstraintViolations(convertedConstraintViolationList, errors);

        return errors;
    }

    @SuppressWarnings("unchecked")
    private ConstraintViolation<Object> asObjectConstraintViolation(ConstraintViolation<?> constraintViolation) {
        return (ConstraintViolation<Object>) constraintViolation;
    }

    private static class ValidatorConverter extends CustomValidatorBean {

        @Override
        public void processConstraintViolations(Set<ConstraintViolation<Object>> violations, Errors errors) {
            super.processConstraintViolations(violations, errors);
        }
    }
}
