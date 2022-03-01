package net.croz.nrich.encrypt.starter.configuration;

import net.croz.nrich.encrypt.api.service.DataEncryptionService;
import net.croz.nrich.encrypt.api.service.TextEncryptionService;
import net.croz.nrich.encrypt.aspect.EncryptDataAspect;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class NrichEncryptAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(NrichEncryptAutoConfiguration.class));

    @Test
    void shouldConfigureDefaultConfiguration() {
        // expect
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(TextEncryptionService.class);
            assertThat(context).hasSingleBean(DataEncryptionService.class);
            assertThat(context).hasSingleBean(EncryptDataAspect.class);
        });
    }

    @Test
    void shouldNotCreateAspectWhenCreationIsDisabled() {
        // expect
        contextRunner.withPropertyValues("nrich.encrypt.encrypt-aspect-enabled=false").run(context -> assertThat(context).doesNotHaveBean(EncryptDataAspect.class));
    }

    @Test
    void shouldCreateAdvisorWhenEncryptConfigurationIsDefined() {
        // expect
        contextRunner.withPropertyValues(
            "nrich.encrypt.encryption-configuration-list[0].method-to-encrypt-decrypt=methodToEncrypt",
            "nrich.encrypt.encryption-configuration-list[0].property-to-encrypt-decrypt-list=property",
            "nrich.encrypt.encryption-configuration-list[0].encryption-operation=ENCRYPT").run(context -> assertThat(context).hasBean("encryptAdvisor"));
    }

    @CsvSource({ "'',''", "ec49c585b7c44f2b,''", "'',ec49c585b7c44f2b" })
    @ParameterizedTest
    void shouldCreateTextEncryptionServiceWithProvidedOrDefaultPasswordAndSalt(String password, String salt) {
        // expect
        contextRunner.withPropertyValues("nrich.encrypt.encrypt-password=" + password, "nrich.encrypt.encrypt-salt=" + salt).run(context -> assertThat(context).hasSingleBean(TextEncryptionService.class));
    }
}
