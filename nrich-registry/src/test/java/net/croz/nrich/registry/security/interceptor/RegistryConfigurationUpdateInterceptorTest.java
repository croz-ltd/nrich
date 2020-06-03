package net.croz.nrich.registry.security.interceptor;

import net.croz.nrich.registry.RegistryTestConfiguration;
import net.croz.nrich.registry.security.exception.RegistryUpdateNotAllowedException;
import net.croz.nrich.registry.security.stub.RegistryConfigurationUpdateInterceptorTestEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;

import static net.croz.nrich.registry.security.testutil.RegistrySecurityGeneratingUtil.createRegistryServiceRequest;
import static net.croz.nrich.registry.security.testutil.RegistrySecurityGeneratingUtil.deleteRegistryRequest;
import static net.croz.nrich.registry.security.testutil.RegistrySecurityGeneratingUtil.updateRegistryServiceRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.catchThrowable;

@SpringJUnitWebConfig(RegistryTestConfiguration.class)
public class RegistryConfigurationUpdateInterceptorTest {

    private static final String CLASS_NAME_OF_NON_READ_ONLY_ENTITY = "some.class.name";

    @Autowired
    private RegistryConfigurationUpdateInterceptor registryConfigurationUpdateInterceptor;

    @Test
    void shouldNotThrowExceptionWhenCreatingEntityThatIsNotReadOnly() {
        // expect
        assertThatCode(() -> registryConfigurationUpdateInterceptor.beforeRegistryCreate(createRegistryServiceRequest(CLASS_NAME_OF_NON_READ_ONLY_ENTITY))).doesNotThrowAnyException();
    }

    @Test
    void shouldThrowExceptionWhenTryingToCreateReadOnlyEntity() {
        // when
        final Throwable thrown = catchThrowable(() -> registryConfigurationUpdateInterceptor.beforeRegistryCreate(createRegistryServiceRequest(RegistryConfigurationUpdateInterceptorTestEntity.class.getName())));

        // then
        assertThat(thrown).isNotNull();
        assertThat(thrown).isInstanceOf(RegistryUpdateNotAllowedException.class);
    }

    @Test
    void shouldNotThrowExceptionWhenUpdatingEntityThatIsNotReadOnly() {
        // expect
        assertThatCode(() -> registryConfigurationUpdateInterceptor.beforeRegistryUpdate(updateRegistryServiceRequest(CLASS_NAME_OF_NON_READ_ONLY_ENTITY))).doesNotThrowAnyException();
    }

    @Test
    void shouldThrowExceptionWhenTryingToUpdateReadOnlyEntity() {
        // when
        final Throwable thrown = catchThrowable(() -> registryConfigurationUpdateInterceptor.beforeRegistryUpdate(updateRegistryServiceRequest(RegistryConfigurationUpdateInterceptorTestEntity.class.getName())));

        // then
        assertThat(thrown).isNotNull();
        assertThat(thrown).isInstanceOf(RegistryUpdateNotAllowedException.class);
    }

    @Test
    void shouldNotThrowExceptionWhenDeletingEntityThatIsNotReadOnly() {
        // expect
        assertThatCode(() -> registryConfigurationUpdateInterceptor.beforeRegistryDelete(deleteRegistryRequest(CLASS_NAME_OF_NON_READ_ONLY_ENTITY))).doesNotThrowAnyException();
    }

    @Test
    void shouldThrowExceptionWhenTryingToDeleteReadOnlyEntity() {
        // when
        final Throwable thrown = catchThrowable(() -> registryConfigurationUpdateInterceptor.beforeRegistryDelete(deleteRegistryRequest(RegistryConfigurationUpdateInterceptorTestEntity.class.getName())));

        // then
        assertThat(thrown).isNotNull();
        assertThat(thrown).isInstanceOf(RegistryUpdateNotAllowedException.class);
    }
}
