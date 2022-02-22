package net.croz.nrich.encrypt.starter.configuration;

import net.croz.nrich.encrypt.api.service.DataEncryptionService;
import net.croz.nrich.encrypt.api.service.TextEncryptionService;
import net.croz.nrich.encrypt.aspect.EncryptDataAspect;
import net.croz.nrich.encrypt.aspect.EncryptMethodInterceptor;
import net.croz.nrich.encrypt.service.BytesEncryptorTextEncryptService;
import net.croz.nrich.encrypt.service.DefaultDataEncryptService;
import net.croz.nrich.encrypt.starter.properties.NrichEncryptProperties;
import net.croz.nrich.encrypt.util.PointcutResolvingUtil;
import net.croz.nrich.springboot.condition.ConditionalOnPropertyNotEmpty;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.util.StringUtils;

@EnableConfigurationProperties(NrichEncryptProperties.class)
@Configuration(proxyBeanMethods = false)
public class NrichEncryptAutoConfiguration {

    @ConditionalOnMissingBean
    @Bean
    public TextEncryptionService textEncryptionService(NrichEncryptProperties encryptProperties) {
        BytesEncryptor encryptor;
        if (!StringUtils.isEmpty(encryptProperties.getEncryptPassword()) && !StringUtils.isEmpty(encryptProperties.getEncryptSalt())) {
            encryptor = Encryptors.standard(encryptProperties.getEncryptPassword(), encryptProperties.getEncryptSalt());
        }
        else {
            encryptor = Encryptors.standard(KeyGenerators.string().generateKey(), KeyGenerators.string().generateKey());
        }

        return new BytesEncryptorTextEncryptService(encryptor, encryptProperties.getTextEncryptCharset());
    }

    @ConditionalOnMissingBean
    @Bean
    public DataEncryptionService dataEncryptionService(TextEncryptionService textEncryptionService) {
        return new DefaultDataEncryptService(textEncryptionService);
    }

    @ConditionalOnProperty(name = "nrich.encrypt.encrypt-aspect-enabled", havingValue = "true", matchIfMissing = true)
    @Bean
    public EncryptDataAspect encryptDataAspect(DataEncryptionService dataEncryptionService) {
        return new EncryptDataAspect(dataEncryptionService);
    }

    @ConditionalOnProperty(name = "nrich.encrypt.encrypt-advisor-enabled", havingValue = "true", matchIfMissing = true)
    @ConditionalOnPropertyNotEmpty("nrich.encrypt.encryption-configuration-list")
    @Bean
    public Advisor encryptAdvisor(DataEncryptionService dataEncryptionService, NrichEncryptProperties encryptProperties) {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();

        pointcut.setExpression(PointcutResolvingUtil.resolvePointcutFromEncryptionConfigurationList(encryptProperties.getEncryptionConfigurationList()));

        return new DefaultPointcutAdvisor(pointcut, new EncryptMethodInterceptor(dataEncryptionService, encryptProperties.getEncryptionConfigurationList(), encryptProperties.getIgnoredMethodList()));
    }
}
