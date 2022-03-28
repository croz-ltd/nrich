package net.croz.nrich.spring.util;

import net.croz.nrich.spring.SpringTestConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig(SpringTestConfiguration.class)
class ApplicationContextHolderTest {

    @Test
    void shouldResolveApplicationContext() {
        // when
        ApplicationContext applicationContext = ApplicationContextHolder.getApplicationContext();

        // then
        assertThat(applicationContext).isNotNull();
    }
}
