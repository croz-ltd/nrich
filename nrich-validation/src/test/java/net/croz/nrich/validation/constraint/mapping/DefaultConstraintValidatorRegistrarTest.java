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

package net.croz.nrich.validation.constraint.mapping;

import net.croz.nrich.validation.constraint.mapping.stub.DefaultConstraintMappingRegistrarConstraint;
import net.croz.nrich.validation.constraint.mapping.stub.DefaultConstraintMappingRegistrarConstraintValidator;
import org.hibernate.validator.HibernateValidatorConfiguration;
import org.hibernate.validator.cfg.ConstraintMapping;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import jakarta.validation.Configuration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

class DefaultConstraintValidatorRegistrarTest {

    private final DefaultConstraintValidatorRegistrar defaultConstraintMappingRegistrar = new DefaultConstraintValidatorRegistrar(List.of("net.croz.nrich.validation.constraint.mapping.stub"));

    @Test
    void shouldNotFailOnNonHibernateConfigurationInstance() {
        // given
        Configuration<?> configuration = mock(Configuration.class);

        // when
        Throwable thrown = catchThrowable(() -> defaultConstraintMappingRegistrar.registerConstraintValidators(configuration));

        // then
        assertThat(thrown).isNull();
        verifyNoInteractions(configuration);
    }

    @Test
    void shouldRegisterConstraints() {
        // given
        HibernateValidatorConfiguration configuration = mock(HibernateValidatorConfiguration.class);
        ConstraintMapping constraintMapping = mock(ConstraintMapping.class, Mockito.RETURNS_DEEP_STUBS);

        doReturn(constraintMapping).when(configuration).createConstraintMapping();

        // when
        defaultConstraintMappingRegistrar.registerConstraintValidators(configuration);

        // then
        verify(constraintMapping.constraintDefinition(DefaultConstraintMappingRegistrarConstraint.class)).validatedBy(DefaultConstraintMappingRegistrarConstraintValidator.class);
    }
}
