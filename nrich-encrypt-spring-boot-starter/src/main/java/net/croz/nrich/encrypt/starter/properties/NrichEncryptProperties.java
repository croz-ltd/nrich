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

package net.croz.nrich.encrypt.starter.properties;

import lombok.Getter;
import net.croz.nrich.encrypt.api.model.EncryptionConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.util.List;

@Getter
@ConstructorBinding
@ConfigurationProperties("nrich.encrypt")
public class NrichEncryptProperties {

    /**
     * Configuration list containing methods for encryption and decryption.
     */
    private final List<EncryptionConfiguration> encryptionConfigurationList;

    /**
     * Used in conjunction with encryptionConfigurationList. It allows defining methods that will not be encrypted. Methods should be in format: fullyQualifiedClasName.methodName.
     */
    private final List<String> ignoredMethodList;

    /**
     * Charset to use for encryption.
     */
    private final String textEncryptCharset;

    /**
     * Whether an aspect bean {@link net.croz.nrich.encrypt.aspect.EncryptDataAspect} that handles encryption for {@link net.croz.nrich.encrypt.api.annotation.EncryptResult}
     * and {@link net.croz.nrich.encrypt.api.annotation.DecryptArgument} is active.
     */
    private final boolean encryptAspectEnabled;

    /**
     * Whether an advisor bean {@link org.springframework.aop.Advisor} that handles encryption from is encryptionConfigurationList os active.
     */
    private final boolean encryptAdvisorEnabled;

    /**
     * Optional parameter. If it is null data is encrypted with randomly generated password on each application restart. If encrypted data
     * will be persisted this parameter should be specified.
     */
    private final String encryptPassword;

    /**
     * Optional parameter. If it is null data is encrypted with randomly generated salt on each application restart. If encrypted data
     * will be persisted this parameter should be specified.
     */
    private final String encryptSalt;

    public NrichEncryptProperties(List<EncryptionConfiguration> encryptionConfigurationList, List<String> ignoredMethodList, @DefaultValue("UTF-8") String textEncryptCharset,
                                  @DefaultValue("true") boolean encryptAspectEnabled, @DefaultValue("true") boolean encryptAdvisorEnabled, String encryptPassword, String encryptSalt) {
        this.encryptionConfigurationList = encryptionConfigurationList;
        this.ignoredMethodList = ignoredMethodList;
        this.textEncryptCharset = textEncryptCharset;
        this.encryptAspectEnabled = encryptAspectEnabled;
        this.encryptAdvisorEnabled = encryptAdvisorEnabled;
        this.encryptPassword = encryptPassword;
        this.encryptSalt = encryptSalt;
    }
}
