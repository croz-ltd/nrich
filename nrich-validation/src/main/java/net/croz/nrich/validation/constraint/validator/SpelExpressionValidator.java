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

import net.croz.nrich.validation.api.constraint.SpelExpression;
import org.springframework.context.ApplicationContext;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SpelExpressionValidator implements ConstraintValidator<SpelExpression, Object> {

    private String spelExpression;

    private ExpressionParser expressionParser;

    private StandardEvaluationContext evaluationContext;

    private ApplicationContext applicationContext;

    public SpelExpressionValidator(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void initialize(SpelExpression constraintAnnotation) {
        spelExpression = constraintAnnotation.value();
        expressionParser = new SpelExpressionParser();
        evaluationContext = new StandardEvaluationContext();
        evaluationContext.setBeanResolver(new BeanFactoryResolver(applicationContext));
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        // will be validated by other constraints
        if (value == null) {
            return true;
        }

        Expression expression = expressionParser.parseExpression(spelExpression);

        return expression.getValue(evaluationContext, value, Boolean.class);
    }
}
