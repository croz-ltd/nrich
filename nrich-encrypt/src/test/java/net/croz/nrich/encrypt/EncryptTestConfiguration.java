package net.croz.nrich.encrypt;

import net.croz.nrich.encrypt.aspect.EncryptDataAspect;
import net.croz.nrich.encrypt.aspect.EncryptionMethodInterceptor;
import net.croz.nrich.encrypt.aspect.stub.EncryptDataAspectTestService;
import net.croz.nrich.encrypt.aspect.stub.EncryptDataAspectTestServiceImpl;
import net.croz.nrich.encrypt.aspect.stub.EncryptionMethodInterceptorTestService;
import net.croz.nrich.encrypt.aspect.stub.EncryptionMethodInterceptorTestServiceImpl;
import net.croz.nrich.encrypt.constants.EncryptConstants;
import net.croz.nrich.encrypt.model.EncryptionConfiguration;
import net.croz.nrich.encrypt.model.EncryptionOperation;
import net.croz.nrich.encrypt.service.DataEncryptionService;
import net.croz.nrich.encrypt.service.TextEncryptionService;
import net.croz.nrich.encrypt.service.aes.AesTextEncryptionService;
import net.croz.nrich.encrypt.service.impl.DataEncryptionServiceImpl;
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
import java.util.stream.Collectors;

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
    public Advisor encryptorAdvisor(final DataEncryptionService dataEncryptionService) {
        final List<String> propertyList = Collections.singletonList("value");
        final List<EncryptionConfiguration> encryptionConfigurationList = Arrays.asList(
                new EncryptionConfiguration("net.croz.nrich.encrypt.aspect.stub.EncryptDataAspectTestServiceImpl.dataToEncryptFromConfiguration", propertyList, EncryptionOperation.ENCRYPT),
                new EncryptionConfiguration("net.croz.nrich.encrypt.aspect.stub.EncryptDataAspectTestServiceImpl.dataToDecryptFromConfiguration", propertyList, EncryptionOperation.DECRYPT),
                new EncryptionConfiguration("net.croz.nrich.encrypt.aspect.stub.EncryptionMethodInterceptorTestServiceImpl.*", propertyList, EncryptionOperation.ENCRYPT),
                new EncryptionConfiguration("net.croz.nrich.encrypt.aspect.stub.EncryptionMethodInterceptorTestServiceImpl.*", propertyList, EncryptionOperation.DECRYPT)
        );

        final AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();

        pointcut.setExpression(buildPointcutExpression(encryptionConfigurationList));

        return new DefaultPointcutAdvisor(pointcut, new EncryptionMethodInterceptor(dataEncryptionService, encryptionConfigurationList, Collections.singletonList("net.croz.nrich.encrypt.aspect.stub.EncryptionMethodInterceptorTestServiceImpl.ignoredMethod")));
    }

    @Bean
    public EncryptDataAspectTestService encryptDataAspectTestService() {
        return new EncryptDataAspectTestServiceImpl();
    }

    @Bean
    public EncryptionMethodInterceptorTestService encryptionMethodInterceptorTestService() {
        return new EncryptionMethodInterceptorTestServiceImpl();
    }


    private String buildPointcutExpression(final List<EncryptionConfiguration> encryptionConfigurationList) {
        return encryptionConfigurationList.stream()
                .map(EncryptionConfiguration::getMethodToEncryptDecrypt)
                .map(method -> String.format(EncryptConstants.EXECUTION_METHOD_POINTCUT, method))
                .collect(Collectors.joining(EncryptConstants.EXECUTION_METHOD_OR_SEPARATOR));
    }

}
