package net.croz.nrich.encrypt.service.aes;

import net.croz.nrich.encrypt.EncryptTestConfiguration;
import net.croz.nrich.encrypt.exception.EncryptionOperationFailedException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@SpringJUnitConfig(EncryptTestConfiguration.class)
public class AesTextEncryptionServiceTest {

    @Autowired
    private AesTextEncryptionService aesTextEncryptionService;

    @Test
    void shouldEncryptDecryptData() {
        // given
        final String textToEncrypt = "My text with different characters, for example: čćžšzp []{}%^*()(!@# 1";

        // when
        final String encryptedText = aesTextEncryptionService.encryptText(textToEncrypt);

        // then
        assertThat(encryptedText).isNotEqualTo(textToEncrypt);

        // and when
        final String decryptedText = aesTextEncryptionService.decryptText(encryptedText);

        // then
        assertThat(decryptedText).isEqualTo(textToEncrypt);
    }

    @Test
    void shouldThrowEncryptionOperationFailedExceptionWhenEncryptingInvalidData() {
        // given
        final String textToEncrypt = null;

        // when
        final Throwable thrown = catchThrowable(() -> aesTextEncryptionService.encryptText(textToEncrypt));

        // then
        assertThat(thrown).isNotNull();
        assertThat(thrown).isInstanceOf(EncryptionOperationFailedException.class);
    }

    @Test
    void shouldThrowEncryptionOperationFailedExceptionWhenDecryptingInvalidData() {
        // given
        final String textToDecrypt = "Non encrypted text";

        // when
        final Throwable thrown = catchThrowable(() -> aesTextEncryptionService.decryptText(textToDecrypt));

        // then
        assertThat(thrown).isNotNull();
        assertThat(thrown).isInstanceOf(EncryptionOperationFailedException.class);
    }

}
