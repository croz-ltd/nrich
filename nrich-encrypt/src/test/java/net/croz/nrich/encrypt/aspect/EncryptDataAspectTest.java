package net.croz.nrich.encrypt.aspect;

import lombok.SneakyThrows;
import net.croz.nrich.encrypt.EncryptTestConfiguration;
import net.croz.nrich.encrypt.aspect.stub.EncryptDataAspectTestService;
import net.croz.nrich.encrypt.aspect.stub.EncryptDataAspectTestServiceResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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

    @SneakyThrows
    @Test
    void shouldEncryptCompletableFutureData() {
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
}
