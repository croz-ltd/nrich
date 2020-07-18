package net.croz.nrich.encrypt.aspect;

import net.croz.nrich.encrypt.EncryptTestConfiguration;
import net.croz.nrich.encrypt.aspect.stub.EncryptDataAspectTestService;
import net.croz.nrich.encrypt.aspect.stub.EncryptDataAspectTestServiceResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig(EncryptTestConfiguration.class)
public class EncryptDataAspectTest {

    @Autowired
    private EncryptDataAspectTestService encryptDataAspectTestService;

    @Test
    void shouldEncryptDecryptData() {
        // given
        final String text = "some text";

        // when
        final EncryptDataAspectTestServiceResult result = encryptDataAspectTestService.dataToEncrypt(text);

        // then
        assertThat(result.getValue()).isNotEqualTo(text);

        // and when
        final EncryptDataAspectTestServiceResult decryptResult = encryptDataAspectTestService.dataToDecrypt(result);

        // then
        assertThat(decryptResult.getValue()).isEqualTo(text);
    }

    @Test
    void shouldEncryptCompletableFutureData() throws Exception {
        // given
        final String text = "some text";

        // when
        final EncryptDataAspectTestServiceResult result = encryptDataAspectTestService.dataToEncryptWithCompletableFuture(text).get();

        // then
        assertThat(result.getValue()).isNotEqualTo(text);

        // and when
        final EncryptDataAspectTestServiceResult decryptResult = encryptDataAspectTestService.dataToDecrypt(result);

        // then
        assertThat(decryptResult.getValue()).isEqualTo(text);
    }

    @Test
    void shouldEncryptMonoData() {
        // given
        final String text = "some text";

        // when
        final EncryptDataAspectTestServiceResult result = encryptDataAspectTestService.dataToEncryptWithMono(text).block();

        // then
        assertThat(result).isNotNull();
        assertThat(result.getValue()).isNotEqualTo(text);

        // and when
        final EncryptDataAspectTestServiceResult decryptResult = encryptDataAspectTestService.dataToDecrypt(result);

        // then
        assertThat(decryptResult.getValue()).isEqualTo(text);
    }

    @Test
    void shouldEncryptFluxData() {
        // given
        final String text = "some text";

        // when
        final EncryptDataAspectTestServiceResult result = encryptDataAspectTestService.dataToEncryptWithFlux(text).blockFirst();

        // then
        assertThat(result).isNotNull();
        assertThat(result.getValue()).isNotEqualTo(text);

        // and when
        final EncryptDataAspectTestServiceResult decryptResult = encryptDataAspectTestService.dataToDecrypt(result);

        // then
        assertThat(decryptResult.getValue()).isEqualTo(text);
    }

    @Test
    void shouldEncryptText() {
        final String text = "some text";

        // when
        final String result = encryptDataAspectTestService.textToEncrypt(text);

        // then
        assertThat(result).isNotNull();
        assertThat(result).isNotEqualTo(text);

        // and when
        final String decryptResult = encryptDataAspectTestService.textToDecrypt(result, "ignored value");

        // then
        assertThat(decryptResult).isEqualTo(text);
    }
}
