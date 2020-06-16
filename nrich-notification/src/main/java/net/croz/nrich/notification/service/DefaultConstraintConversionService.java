package net.croz.nrich.notification.service;

import net.croz.nrich.notification.service.ConstraintConversionService;
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
    public Object resolveTarget(final Set<ConstraintViolation<?>> constraintViolationList) {
        return constraintViolationList.stream()
                .map(ConstraintViolation::getLeafBean)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    @Override
    public Errors convertConstraintViolationsToErrors(final Set<ConstraintViolation<?>> constraintViolationList, final Object target, final String targetName) {
        final BeanPropertyBindingResult errors = new BeanPropertyBindingResult(target, targetName);

        final Set<ConstraintViolation<Object>> convertedConstraintViolationList = constraintViolationList.stream().map(this::asObjectConstraintViolation).collect(Collectors.toSet());

        validatorConverter.processConstraintViolations(convertedConstraintViolationList, errors);

        return errors;
    }

    private static class ValidatorConverter extends CustomValidatorBean {

        @Override
        public void processConstraintViolations(final Set<ConstraintViolation<Object>> violations, final Errors errors) {
            super.processConstraintViolations(violations, errors);
        }
    }

    @SuppressWarnings("unchecked")
    private ConstraintViolation<Object> asObjectConstraintViolation(final ConstraintViolation<?> constraintViolation) {
        return (ConstraintViolation<Object>) constraintViolation;
    }
}
