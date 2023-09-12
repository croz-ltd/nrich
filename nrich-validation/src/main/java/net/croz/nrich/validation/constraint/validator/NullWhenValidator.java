/*
 *  Copyright 2020-2023 CROZ d.o.o, the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package net.croz.nrich.validation.constraint.validator;

import net.croz.nrich.validation.api.constraint.NullWhen;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
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
