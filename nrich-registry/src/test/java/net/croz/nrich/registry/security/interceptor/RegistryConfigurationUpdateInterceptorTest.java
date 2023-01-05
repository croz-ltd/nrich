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

package net.croz.nrich.registry.security.interceptor;

import net.croz.nrich.registry.RegistryTestConfiguration;
import net.croz.nrich.registry.api.security.exception.RegistryUpdateNotAllowedException;
import net.croz.nrich.registry.security.stub.RegistryConfigurationUpdateInterceptorNonModifiableEntity;
import net.croz.nrich.registry.security.stub.RegistryConfigurationUpdateInterceptorTestEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.catchThrowable;

@SpringJUnitWebConfig(RegistryTestConfiguration.class)
class RegistryConfigurationUpdateInterceptorTest {

    private static final String CLASS_NAME_OF_NON_READ_ONLY_ENTITY = "some.class.name";

    @Autowired
    private RegistryConfigurationUpdateInterceptor registryConfigurationUpdateInterceptor;

    @Test
    void shouldNotThrowExceptionWhenCreatingEntityThatIsNotReadOnly() {
        // expect
        assertThatCode(() -> registryConfigurationUpdateInterceptor.beforeRegistryCreate(CLASS_NAME_OF_NON_READ_ONLY_ENTITY, null)).doesNotThrowAnyException();
    }

    @ValueSource(classes = { RegistryConfigurationUpdateInterceptorTestEntity.class, RegistryConfigurationUpdateInterceptorNonModifiableEntity.class })
    @ParameterizedTest
    void shouldThrowExceptionWhenTryingToCreateNonCreatableEntity(Class<?> type) {
        // when
        Throwable thrown = catchThrowable(() -> registryConfigurationUpdateInterceptor.beforeRegistryCreate(type.getName(), null));

        // then
        assertThat(thrown).isInstanceOf(RegistryUpdateNotAllowedException.class);
    }

    @Test
    void shouldNotThrowExceptionWhenUpdatingEntityThatIsNotReadOnly() {
        // expect
        assertThatCode(() -> registryConfigurationUpdateInterceptor.beforeRegistryUpdate(CLASS_NAME_OF_NON_READ_ONLY_ENTITY, null, null)).doesNotThrowAnyException();
    }

    @ValueSource(classes = { RegistryConfigurationUpdateInterceptorTestEntity.class, RegistryConfigurationUpdateInterceptorNonModifiableEntity.class })
    @ParameterizedTest
    void shouldThrowExceptionWhenTryingToUpdateNonUpdatableEntity(Class<?> type) {
        // when
        Throwable thrown = catchThrowable(() -> registryConfigurationUpdateInterceptor.beforeRegistryUpdate(type.getName(), null, null));

        // then
        assertThat(thrown).isInstanceOf(RegistryUpdateNotAllowedException.class);
    }

    @Test
    void shouldNotThrowExceptionWhenDeletingEntityThatIsNotReadOnly() {
        // expect
        assertThatCode(() -> registryConfigurationUpdateInterceptor.beforeRegistryDelete(CLASS_NAME_OF_NON_READ_ONLY_ENTITY, null)).doesNotThrowAnyException();
    }

    @ValueSource(classes = { RegistryConfigurationUpdateInterceptorTestEntity.class, RegistryConfigurationUpdateInterceptorNonModifiableEntity.class })
    @ParameterizedTest
    void shouldThrowExceptionWhenTryingToDeleteNonDeletableEntity(Class<?> type) {
        // when
        Throwable thrown = catchThrowable(() -> registryConfigurationUpdateInterceptor.beforeRegistryDelete(type.getName(), null));

        // then
        assertThat(thrown).isInstanceOf(RegistryUpdateNotAllowedException.class);
    }

    @Test
    void shouldNotFailWithEmptyConfiguration() {
        // expect
        assertThatCode(() -> new RegistryConfigurationUpdateInterceptor(null).beforeRegistryDelete(CLASS_NAME_OF_NON_READ_ONLY_ENTITY, null)).doesNotThrowAnyException();
    }
}
