package net.croz.nrich.spring.util;

import net.croz.nrich.spring.SpringUtilTestConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig(SpringUtilTestConfiguration.class)
class ApplicationContextHolderTest {

    @Test
    void shouldResolveApplicationContext() {
        // when
        ApplicationContext applicationContext = ApplicationContextHolder.getApplicationContext();

        // then
        assertThat(applicationContext).isNotNull();
    }
}
