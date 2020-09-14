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
        final String textToEncrypt = "My text with different characters, for example: čćžšzp []{}%^*()(!@# 1";

        // when
        final String encryptedText = textEncryptionService.encryptText(textToEncrypt);

        // then
        assertThat(encryptedText).isNotEqualTo(textToEncrypt);

        // and when
        final String decryptedText = textEncryptionService.decryptText(encryptedText);

        // then
        assertThat(decryptedText).isEqualTo(textToEncrypt);
    }

    @Test
    void shouldThrowEncryptionOperationFailedExceptionWhenEncryptingInvalidData() {
        // given
        final String textToEncrypt = null;

        // when
        final Throwable thrown = catchThrowable(() -> textEncryptionService.encryptText(textToEncrypt));

        // then
        assertThat(thrown).isInstanceOf(EncryptOperationFailedException.class);
    }

    @Test
    void shouldThrowEncryptionOperationFailedExceptionWhenDecryptingInvalidData() {
        // given
        final String textToDecrypt = "Non encrypted text";

        // when
        final Throwable thrown = catchThrowable(() -> textEncryptionService.decryptText(textToDecrypt));

        // then
        assertThat(thrown).isInstanceOf(EncryptOperationFailedException.class);
    }

}
