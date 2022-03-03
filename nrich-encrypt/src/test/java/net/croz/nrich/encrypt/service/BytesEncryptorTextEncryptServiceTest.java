package net.croz.nrich.encrypt.service;

import net.croz.nrich.encrypt.EncryptTestConfiguration;
import net.croz.nrich.encrypt.exception.EncryptOperationFailedException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@SpringJUnitConfig(EncryptTestConfiguration.class)
class BytesEncryptorTextEncryptServiceTest {

    @Autowired
    private BytesEncryptorTextEncryptService textEncryptionService;

    @Test
    void shouldEncryptDecryptData() {
        // given
        String textToEncrypt = "My text with different characters, for example: čćžšzp []{}%^*()(!@# 1";

        // when
        String encryptedText = textEncryptionService.encryptText(textToEncrypt);

        // then
        assertThat(encryptedText).isNotEqualTo(textToEncrypt);

        // and when
        String decryptedText = textEncryptionService.decryptText(encryptedText);

        // then
        assertThat(decryptedText).isEqualTo(textToEncrypt);
    }

    @Test
    void shouldThrowEncryptionOperationFailedExceptionWhenEncryptingInvalidData() {
        // given
        String textToEncrypt = null;

        // when
        Throwable thrown = catchThrowable(() -> textEncryptionService.encryptText(textToEncrypt));

        // then
        assertThat(thrown).isInstanceOf(EncryptOperationFailedException.class);
    }

    @Test
    void shouldThrowEncryptionOperationFailedExceptionWhenDecryptingInvalidData() {
        // given
        String textToDecrypt = "Non encrypted text";

        // when
        Throwable thrown = catchThrowable(() -> textEncryptionService.decryptText(textToDecrypt));

        // then
        assertThat(thrown).isInstanceOf(EncryptOperationFailedException.class);
    }
}
