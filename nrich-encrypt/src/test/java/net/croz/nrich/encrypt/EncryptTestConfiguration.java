package net.croz.nrich.encrypt;

import net.croz.nrich.encrypt.api.model.EncryptionConfiguration;
import net.croz.nrich.encrypt.api.model.EncryptionOperation;
import net.croz.nrich.encrypt.api.service.DataEncryptionService;
import net.croz.nrich.encrypt.api.service.TextEncryptionService;
import net.croz.nrich.encrypt.aspect.EncryptDataAspect;
import net.croz.nrich.encrypt.aspect.EncryptMethodInterceptor;
import net.croz.nrich.encrypt.aspect.stub.DefaultEncryptDataAspectTestService;
import net.croz.nrich.encrypt.aspect.stub.DefaultEncryptionMethodInterceptorTestService;
import net.croz.nrich.encrypt.aspect.stub.EncryptDataAspectTestService;
import net.croz.nrich.encrypt.aspect.stub.EncryptionMethodInterceptorTestService;
import net.croz.nrich.encrypt.service.BytesEncryptorTextEncryptService;
import net.croz.nrich.encrypt.service.DefaultDataEncryptService;
import net.croz.nrich.encrypt.util.PointcutResolvingUtil;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.keygen.KeyGenerators;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@EnableAspectJAutoProxy
@Configuration(proxyBeanMethods = false)
public class EncryptTestConfiguration {

    @Bean
    public TextEncryptionService textEncryptionService() {
        BytesEncryptor encryptor = Encryptors.standard(KeyGenerators.string().generateKey(), KeyGenerators.string().generateKey());

        return new BytesEncryptorTextEncryptService(encryptor, "UTF-8");
    }

    @Bean
    public DataEncryptionService dataEncryptionService(TextEncryptionService textEncryptionService) {
        return new DefaultDataEncryptService(textEncryptionService);
    }

    @Bean
    public EncryptDataAspect encryptDataAspect(DataEncryptionService dataEncryptionService) {
        return new EncryptDataAspect(dataEncryptionService);
    }

    @Bean
    public Advisor encryptorAdvisor(DataEncryptionService dataEncryptionService) {
        List<String> propertyList = Collections.singletonList("value");
        List<EncryptionConfiguration> encryptionConfigurationList = Arrays.asList(
            new EncryptionConfiguration("net.croz.nrich.encrypt.aspect.stub.DefaultEncryptDataAspectTestService.dataToEncryptFromConfiguration", propertyList, EncryptionOperation.ENCRYPT),
            new EncryptionConfiguration("net.croz.nrich.encrypt.aspect.stub.DefaultEncryptDataAspectTestService.dataToDecryptFromConfiguration", propertyList, EncryptionOperation.DECRYPT),
            new EncryptionConfiguration("net.croz.nrich.encrypt.aspect.stub.DefaultEncryptionMethodInterceptorTestService.*", propertyList, EncryptionOperation.ENCRYPT),
            new EncryptionConfiguration("net.croz.nrich.encrypt.aspect.stub.DefaultEncryptionMethodInterceptorTestService.*", propertyList, EncryptionOperation.DECRYPT)
        );

        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();

        pointcut.setExpression(PointcutResolvingUtil.resolvePointcutFromEncryptionConfigurationList(encryptionConfigurationList));

        return new DefaultPointcutAdvisor(pointcut, new EncryptMethodInterceptor(dataEncryptionService, encryptionConfigurationList, Collections.singletonList("net.croz.nrich.encrypt.aspect.stub.DefaultEncryptionMethodInterceptorTestService.ignoredMethod")));
    }

    @Bean
    public EncryptDataAspectTestService encryptDataAspectTestService() {
        return new DefaultEncryptDataAspectTestService();
    }

    @Bean
    public EncryptionMethodInterceptorTestService encryptionMethodInterceptorTestService() {
        return new DefaultEncryptionMethodInterceptorTestService();
    }
}
