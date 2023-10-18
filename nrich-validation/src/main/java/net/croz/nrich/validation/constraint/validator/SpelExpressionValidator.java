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
import org.springframework.context.expression.EnvironmentAccessor;
import org.springframework.context.expression.MapAccessor;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelCompilerMode;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SpelExpressionValidator implements ConstraintValidator<SpelExpression, Object> {

    private static final String ENVIRONMENT_VARIABLE_NAME = "environment";

    private static final String SYSTEM_PROPERTIES_VARIABLE_NAME = "systemProperties";

    private String spelExpression;

    private final ExpressionParser expressionParser;

    private final EvaluationContext evaluationContext;

    public SpelExpressionValidator(ApplicationContext applicationContext) {
        expressionParser = new SpelExpressionParser();
        evaluationContext = createEvaluationContext(applicationContext);
    }

    @Override
    public void initialize(SpelExpression constraintAnnotation) {
        spelExpression = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        // will be validated by other constraints
        if (value == null) {
            return true;
        }

        Expression expression = expressionParser.parseExpression(spelExpression);

        return Boolean.TRUE.equals(expression.getValue(evaluationContext, value, Boolean.class));
    }

    private EvaluationContext createEvaluationContext(ApplicationContext applicationContext) {
        SpelParserConfiguration config = new SpelParserConfiguration(SpelCompilerMode.MIXED, this.getClass().getClassLoader());
        StandardEvaluationContext standardEvaluationContext = new StandardEvaluationContext(config);

        standardEvaluationContext.setBeanResolver(new BeanFactoryResolver(applicationContext));
        standardEvaluationContext.addPropertyAccessor(new MapAccessor());
        standardEvaluationContext.addPropertyAccessor(new EnvironmentAccessor());

        standardEvaluationContext.setVariable(ENVIRONMENT_VARIABLE_NAME, applicationContext.getEnvironment());
        standardEvaluationContext.setVariable(SYSTEM_PROPERTIES_VARIABLE_NAME, System.getProperties());

        return standardEvaluationContext;
    }
}
