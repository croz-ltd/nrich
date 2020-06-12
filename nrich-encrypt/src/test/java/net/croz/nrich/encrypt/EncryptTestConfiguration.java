package net.croz.nrich.encrypt;

import net.croz.nrich.encrypt.aspect.EncryptDataAspect;
import net.croz.nrich.encrypt.aspect.stub.EncryptDataAspectTestService;
import net.croz.nrich.encrypt.aspect.stub.EncryptDataAspectTestServiceImpl;
import net.croz.nrich.encrypt.service.DataEncryptionService;
import net.croz.nrich.encrypt.service.TextEncryptionService;
import net.croz.nrich.encrypt.service.aes.AesTextEncryptionService;
import net.croz.nrich.encrypt.service.impl.DataEncryptionServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.keygen.KeyGenerators;

@EnableAspectJAutoProxy
@Configuration(proxyBeanMethods = false)
public class EncryptTestConfiguration {

    @Bean
    public TextEncryptionService textEncryptionService() {
        final BytesEncryptor encryptor = Encryptors.standard(KeyGenerators.string().generateKey(), KeyGenerators.string().generateKey());

        return new AesTextEncryptionService(encryptor, "UTF-8");
    }

    @Bean
    public DataEncryptionService dataEncryptionService(final TextEncryptionService textEncryptionService) {
        return new DataEncryptionServiceImpl(textEncryptionService);
    }

    @Bean
    public EncryptDataAspect encryptDataAspect(final DataEncryptionService dataEncryptionService) {
        return new EncryptDataAspect(dataEncryptionService);
    }

    @Bean
    public EncryptDataAspectTestService encryptDataAspectTestService() {
        return new EncryptDataAspectTestServiceImpl();
    }
}
