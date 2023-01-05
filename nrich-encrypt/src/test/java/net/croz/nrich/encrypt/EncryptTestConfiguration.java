/*
 *  Copyright 2020-2023 CROZ d.o.o, the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

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
        String encryptMethodName = "net.croz.nrich.encrypt.aspect.stub.DefaultEncryptDataAspectTestService.dataToEncryptFromConfiguration";
        String decryptMethodName = "net.croz.nrich.encrypt.aspect.stub.DefaultEncryptDataAspectTestService.dataToDecryptFromConfiguration";
        String allInterceptorServiceMethods = "net.croz.nrich.encrypt.aspect.stub.DefaultEncryptionMethodInterceptorTestService.*";
        List<EncryptionConfiguration> encryptionConfigurationList = Arrays.asList(
            new EncryptionConfiguration(encryptMethodName, propertyList, EncryptionOperation.ENCRYPT),
            new EncryptionConfiguration(decryptMethodName, propertyList, EncryptionOperation.DECRYPT),
            new EncryptionConfiguration(allInterceptorServiceMethods, propertyList, EncryptionOperation.ENCRYPT),
            new EncryptionConfiguration(allInterceptorServiceMethods, propertyList, EncryptionOperation.DECRYPT)
        );
        List<String> ignoredMethodList = Collections.singletonList("net.croz.nrich.encrypt.aspect.stub.DefaultEncryptionMethodInterceptorTestService.ignoredMethod");

        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();

        pointcut.setExpression(PointcutResolvingUtil.resolvePointcutFromEncryptionConfigurationList(encryptionConfigurationList));

        return new DefaultPointcutAdvisor(pointcut, new EncryptMethodInterceptor(dataEncryptionService, encryptionConfigurationList, ignoredMethodList));
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
