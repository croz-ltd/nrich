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
import org.springframework.security.crypto.codec.Hex;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.util.ObjectUtils;

@EnableConfigurationProperties(NrichEncryptProperties.class)
@Configuration(proxyBeanMethods = false)
public class NrichEncryptAutoConfiguration {

    @ConditionalOnMissingBean
    @Bean
    public TextEncryptionService textEncryptionService(NrichEncryptProperties encryptProperties) {
        boolean passwordDefined = !ObjectUtils.isEmpty(encryptProperties.encryptPassword());
        boolean saltDefined = !ObjectUtils.isEmpty(encryptProperties.encryptSalt());

        boolean onlyOneConfigured = passwordDefined != saltDefined;
        if (onlyOneConfigured) {
            throw new IllegalStateException("Properties nrich.encrypt.encrypt-password and nrich.encrypt.encrypt-salt must be configured together (or both left empty for a randomly generated key); "
                + "configuring only one results in encryption that cannot be decrypted after an application restart");
        }

        if (saltDefined) {
            validateSaltIsHexEncoded(encryptProperties.encryptSalt());
        }

        String password = passwordDefined ? encryptProperties.encryptPassword() : KeyGenerators.string().generateKey();
        String salt = saltDefined ? encryptProperties.encryptSalt() : KeyGenerators.string().generateKey();
        BytesEncryptor encryptor = Encryptors.standard(password, salt);

        return new BytesEncryptorTextEncryptService(encryptor, encryptProperties.textEncryptCharset());
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

        pointcut.setExpression(PointcutResolvingUtil.resolvePointcutFromEncryptionConfigurationList(encryptProperties.encryptionConfigurationList()));

        return new DefaultPointcutAdvisor(pointcut, new EncryptMethodInterceptor(dataEncryptionService, encryptProperties.encryptionConfigurationList(), encryptProperties.ignoredMethodList()));
    }

    private void validateSaltIsHexEncoded(String salt) {
        try {
            Hex.decode(salt);
        }
        catch (IllegalArgumentException exception) {
            throw new IllegalStateException(
                String.format("Property nrich.encrypt.encrypt-salt must be a hex-encoded string (for example a value generated by KeyGenerators.string().generateKey()), but was: %s", salt), exception
            );
        }
    }
}
