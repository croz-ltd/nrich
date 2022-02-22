package net.croz.nrich.registry.security.interceptor;

import net.croz.nrich.registry.RegistryTestConfiguration;
import net.croz.nrich.registry.api.security.exception.RegistryUpdateNotAllowedException;
import net.croz.nrich.registry.security.stub.RegistryConfigurationUpdateInterceptorTestEntity;
import org.junit.jupiter.api.Test;
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

    @Test
    void shouldThrowExceptionWhenTryingToCreateReadOnlyEntity() {
        // when
        Throwable thrown = catchThrowable(() -> registryConfigurationUpdateInterceptor.beforeRegistryCreate(RegistryConfigurationUpdateInterceptorTestEntity.class.getName(), null));

        // then
        assertThat(thrown).isInstanceOf(RegistryUpdateNotAllowedException.class);
    }

    @Test
    void shouldNotThrowExceptionWhenUpdatingEntityThatIsNotReadOnly() {
        // expect
        assertThatCode(() -> registryConfigurationUpdateInterceptor.beforeRegistryUpdate(CLASS_NAME_OF_NON_READ_ONLY_ENTITY, null, null)).doesNotThrowAnyException();
    }

    @Test
    void shouldThrowExceptionWhenTryingToUpdateReadOnlyEntity() {
        // when
        Throwable thrown = catchThrowable(() -> registryConfigurationUpdateInterceptor.beforeRegistryUpdate(RegistryConfigurationUpdateInterceptorTestEntity.class.getName(), null, null));

        // then
        assertThat(thrown).isInstanceOf(RegistryUpdateNotAllowedException.class);
    }

    @Test
    void shouldNotThrowExceptionWhenDeletingEntityThatIsNotReadOnly() {
        // expect
        assertThatCode(() -> registryConfigurationUpdateInterceptor.beforeRegistryDelete(CLASS_NAME_OF_NON_READ_ONLY_ENTITY, null)).doesNotThrowAnyException();
    }

    @Test
    void shouldThrowExceptionWhenTryingToDeleteReadOnlyEntity() {
        // when
        Throwable thrown = catchThrowable(() -> registryConfigurationUpdateInterceptor.beforeRegistryDelete(RegistryConfigurationUpdateInterceptorTestEntity.class.getName(), null));

        // then
        assertThat(thrown).isInstanceOf(RegistryUpdateNotAllowedException.class);
    }
}
