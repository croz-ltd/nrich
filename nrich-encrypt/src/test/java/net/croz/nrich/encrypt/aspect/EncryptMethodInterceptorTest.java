package net.croz.nrich.encrypt.aspect;

import net.croz.nrich.encrypt.EncryptTestConfiguration;
import net.croz.nrich.encrypt.aspect.stub.EncryptDataAspectTestService;
import net.croz.nrich.encrypt.aspect.stub.EncryptDataAspectTestServiceResult;
import net.croz.nrich.encrypt.aspect.stub.EncryptionMethodInterceptorTestService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig(EncryptTestConfiguration.class)
class EncryptMethodInterceptorTest {

    @Autowired
    private EncryptDataAspectTestService encryptDataAspectTestService;

    @Autowired
    private EncryptionMethodInterceptorTestService encryptionMethodInterceptorTestService;

    @Test
    void shouldEncryptDecryptData() {
        // given
        final String text = "some text";

        // when
        final EncryptDataAspectTestServiceResult result = encryptDataAspectTestService.dataToEncryptFromConfiguration(text);

        // then
        assertThat(result.getValue()).isNotEqualTo(text);

        // and when
        final EncryptDataAspectTestServiceResult decryptResult = encryptDataAspectTestService.dataToDecryptFromConfiguration(result);

        // then
        assertThat(decryptResult.getValue()).isEqualTo(text);
    }

    @Test
    void shouldEncryptDecryptDataWithPointcutMatchingAllMethods() {
        // given
        final String text = "some text";

        // when
        final EncryptDataAspectTestServiceResult result = encryptionMethodInterceptorTestService.dataToEncryptFromConfiguration(text);

        // then
        assertThat(result.getValue()).isNotEqualTo(text);

        // and when
        final String decryptResult = encryptionMethodInterceptorTestService.dataToDecryptFromConfiguration(result);

        // then
        assertThat(decryptResult).isEqualTo(text);
    }

    @Test
    void shouldNotEncryptDataForIgnoredMethod() {
        // given
        final String text = "some text";

        // when
        final EncryptDataAspectTestServiceResult result = encryptionMethodInterceptorTestService.ignoredMethod(text);

        // then
        assertThat(result.getValue()).isEqualTo(text);
    }

    @Test
    void shouldSkipResultEncryptionWhenAnnotationArgumentListIsEmpty() {
        // given
        final String text = "some text";

        // when
        final EncryptDataAspectTestServiceResult result = encryptDataAspectTestService.dataToEncryptWithInvalidAnnotation(text);

        // then
        assertThat(result.getValue()).isEqualTo(text);
    }

    @Test
    void shouldSkipDecryptionWhenAnnotationArgumentListIsEmpty() {
        // given
        final String text = "some text";

        // when
        final EncryptDataAspectTestServiceResult result = encryptDataAspectTestService.dataToEncrypt(text);

        // then
        assertThat(result.getValue()).isNotEqualTo(text);

        // and when
        final EncryptDataAspectTestServiceResult decryptResult = encryptDataAspectTestService.dataToDecryptWithInvalidAnnotation(result);

        // then
        assertThat(decryptResult.getValue()).isEqualTo(result.getValue());
    }
}
