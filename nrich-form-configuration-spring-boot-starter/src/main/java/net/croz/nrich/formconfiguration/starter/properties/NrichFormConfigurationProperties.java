/*
 *  Copyright 2020-2022 CROZ d.o.o, the original author or authors.
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

package net.croz.nrich.formconfiguration.starter.properties;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.util.Map;

@Getter
@ConstructorBinding
@ConfigurationProperties("nrich.form-configuration")
public class NrichFormConfigurationProperties {

    /**
     * Whether default converter service ({@link net.croz.nrich.formconfiguration.service.DefaultConstrainedPropertyValidatorConverterService})
     * for converting {@link net.croz.nrich.formconfiguration.api.model.ConstrainedProperty} instances
     * to client {@link net.croz.nrich.formconfiguration.api.model.ConstrainedPropertyClientValidatorConfiguration} list is enabled.
     */
    private final boolean defaultConverterEnabled;

    /**
     * Mapping between a client side form identifier and class holding the constraints for the form (usually the class accepted as input on the server side).
     */
    private final Map<String, Class<?>> formConfigurationMapping;

    public NrichFormConfigurationProperties(@DefaultValue("true") boolean defaultConverterEnabled, Map<String, Class<?>> formConfigurationMapping) {
        this.defaultConverterEnabled = defaultConverterEnabled;
        this.formConfigurationMapping = formConfigurationMapping;
    }
}
