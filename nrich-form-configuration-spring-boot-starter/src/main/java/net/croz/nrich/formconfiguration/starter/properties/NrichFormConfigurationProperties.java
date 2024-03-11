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

package net.croz.nrich.formconfiguration.starter.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.util.List;
import java.util.Map;

/**
 * @param defaultConverterEnabled                       Whether default converter service ({@link net.croz.nrich.formconfiguration.service.DefaultConstrainedPropertyValidatorConverterService})
 *                                                      for converting {@link net.croz.nrich.formconfiguration.api.model.ConstrainedProperty} instances
 *                                                      to client {@link net.croz.nrich.formconfiguration.api.model.ConstrainedPropertyClientValidatorConfiguration} list is enabled.
 * @param defaultJavaToJavascriptConverterEnabled       Whether default Java to Javascript type converter ({@link net.croz.nrich.javascript.converter.DefaultJavaToJavascriptTypeConverter}) used for converting Java to Javascript types is enabled.
 * @param formConfigurationMapping                      Mapping between a client side form identifier and class holding the constraints for the form (usually the class accepted as input on the server side).
 * @param formValidationConfigurationClassesPackageList Optional package list to scan for {@link net.croz.nrich.formconfiguration.api.annotation.FormValidationConfiguration} annotated classes (if not set annotated classes won't be searched).
 */
@ConfigurationProperties("nrich.form-configuration")
public record NrichFormConfigurationProperties(@DefaultValue("true") boolean defaultConverterEnabled, @DefaultValue("true") boolean defaultJavaToJavascriptConverterEnabled,
                                               Map<String, Class<?>> formConfigurationMapping, List<String> formValidationConfigurationClassesPackageList) {

}
