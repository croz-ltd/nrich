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

package net.croz.nrich.notification.service;

import net.croz.nrich.notification.stub.DefaultConstraintConversionServiceTestRequest;
import org.junit.jupiter.api.Test;
import org.springframework.validation.Errors;

import javax.validation.ConstraintViolation;
import java.util.Collections;
import java.util.Set;

import static net.croz.nrich.notification.testutil.ConstraintViolationGeneratingUtil.createConstraintViolationForTarget;
import static net.croz.nrich.notification.testutil.ConstraintViolationGeneratingUtil.createConstraintViolationForTargetConversion;
import static org.assertj.core.api.Assertions.assertThat;

class DefaultConstraintConversionServiceTest {

    private final DefaultConstraintConversionService defaultConstraintConversionService = new DefaultConstraintConversionService();

    @Test
    void shouldResolveValidationTarget() {
        // given
        DefaultConstraintConversionServiceTestRequest request = new DefaultConstraintConversionServiceTestRequest();
        ConstraintViolation<?> constraintViolation = createConstraintViolationForTarget(request);

        // when
        Object result = defaultConstraintConversionService.resolveTarget(Collections.singleton(constraintViolation));

        // then
        assertThat(result).isInstanceOf(DefaultConstraintConversionServiceTestRequest.class);
    }

    @Test
    void shouldConvertConstraintViolationsToErrors() {
        // given
        DefaultConstraintConversionServiceTestRequest request = new DefaultConstraintConversionServiceTestRequest();
        String targetName = request.getClass().getSimpleName();
        ConstraintViolation<?> constraintViolation = createConstraintViolationForTargetConversion(request);
        Set<ConstraintViolation<?>> constraintViolations = Collections.singleton(constraintViolation);

        // when
        Errors result = defaultConstraintConversionService.convertConstraintViolationsToErrors(constraintViolations, request, targetName);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getAllErrors()).hasSize(1);
        assertThat(result.getAllErrors()).extracting("objectName").containsExactly(targetName);
        assertThat(result.getAllErrors()).flatExtracting("codes").containsExactly(
            "NotNull.DefaultConstraintConversionServiceTestRequest.property", "NotNull.property", "NotNull"
        );
        assertThat(result.getAllErrors()).extracting("defaultMessage").containsExactly("message");
        assertThat(result.getAllErrors()).extracting("field").containsExactly("property");
        assertThat(result.getAllErrors()).extracting("rejectedValue").containsExactly("invalid");
    }
}
