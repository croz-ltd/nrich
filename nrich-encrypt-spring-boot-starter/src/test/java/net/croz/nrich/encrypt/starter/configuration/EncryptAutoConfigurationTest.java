package net.croz.nrich.encrypt.starter.configuration;

import net.croz.nrich.encrypt.api.service.DataEncryptionService;
import net.croz.nrich.encrypt.api.service.TextEncryptionService;
import net.croz.nrich.encrypt.aspect.EncryptDataAspect;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

public class EncryptAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(EncryptAutoConfiguration.class)).withInitializer(new ConfigFileApplicationContextInitializer());

    @Test
    void shouldConfigureDefaultConfiguration() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(TextEncryptionService.class);
            assertThat(context).hasSingleBean(DataEncryptionService.class);
            assertThat(context).hasSingleBean(EncryptDataAspect.class);
        });
    }

    @Test
    void shouldNotCreateAspectWhenCreationIsDisabled() {
        contextRunner.withPropertyValues("nrich.encrypt.configuration.encrypt-aspect-enabled=false").run(context -> assertThat(context).doesNotHaveBean(EncryptDataAspect.class));
    }

    @Test
    void shouldCreateAdvisorWhenEncryptConfigurationIsDefined() {
        contextRunner.withPropertyValues("nrich.encrypt.configuration.encryption-configuration-list[0].method-to-encrypt-decrypt=methodToEncrypt",
                "nrich.encrypt.configuration.encryption-configuration-list[0].property-to-encrypt-decrypt-list=property",
                "nrich.encrypt.configuration.encryption-configuration-list[0].encryption-operation=ENCRYPT").run(context -> assertThat(context).hasBean("encryptAdvisor"));
    }
}
