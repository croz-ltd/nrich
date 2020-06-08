package net.croz.nrich.notification.service;

import org.springframework.validation.Errors;

import javax.validation.ConstraintViolation;
import java.util.Set;

public interface ConstraintConversionService {

    Object resolveTarget(Set<ConstraintViolation<?>> constraintViolationList);

    Errors convertConstraintViolationsToErrors(Set<ConstraintViolation<?>> constraintViolationList, Object target, String targetName);

}
