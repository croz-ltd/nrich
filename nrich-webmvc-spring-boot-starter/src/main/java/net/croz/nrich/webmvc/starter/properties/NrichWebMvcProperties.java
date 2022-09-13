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

package net.croz.nrich.webmvc.starter.properties;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.util.List;

@Getter
@ConfigurationProperties("nrich.webmvc")
public class NrichWebMvcProperties {

    /**
     * Whether {@link net.croz.nrich.webmvc.advice.NotificationErrorHandlingRestControllerAdvice} controller advice is enabled.
     */
    private final boolean controllerAdviceEnabled;

    /**
     * Whether default {@link net.croz.nrich.webmvc.api.service.ExceptionAuxiliaryDataResolverService} is enabled.
     */
    private final boolean exceptionAuxiliaryDataResolvingEnabled;

    /**
     * Whether empty strings should be converted to null when binding requests.
     */
    private final boolean convertEmptyStringsToNull;

    /**
     * Whether transient fields should be ignored when binding requests.
     */
    private final boolean ignoreTransientFields;

    /**
     * List of exceptions that will be unwrapping their cause.
     */
    private final List<String> exceptionToUnwrapList;

    /**
     * List of exception auxiliary data to be included in notification sent to client.
     */
    private final List<String> exceptionAuxiliaryDataToIncludeInNotification;

    /**
     * Optional property. Default locale.
     */
    private final String defaultLocale;

    /**
     * Optional property. List of locales users can set.
     */
    private final List<String> allowedLocaleList;

    public NrichWebMvcProperties(@DefaultValue("true") boolean controllerAdviceEnabled, @DefaultValue("true") boolean exceptionAuxiliaryDataResolvingEnabled,
                                 @DefaultValue("true") boolean convertEmptyStringsToNull, @DefaultValue("true") boolean ignoreTransientFields,
                                 @DefaultValue("java.util.concurrent.ExecutionException") List<String> exceptionToUnwrapList,
                                 @DefaultValue("uuid") List<String> exceptionAuxiliaryDataToIncludeInNotification, String defaultLocale, List<String> allowedLocaleList) {
        this.controllerAdviceEnabled = controllerAdviceEnabled;
        this.exceptionAuxiliaryDataResolvingEnabled = exceptionAuxiliaryDataResolvingEnabled;
        this.convertEmptyStringsToNull = convertEmptyStringsToNull;
        this.ignoreTransientFields = ignoreTransientFields;
        this.exceptionToUnwrapList = exceptionToUnwrapList;
        this.exceptionAuxiliaryDataToIncludeInNotification = exceptionAuxiliaryDataToIncludeInNotification;
        this.defaultLocale = defaultLocale;
        this.allowedLocaleList = allowedLocaleList;
    }
}
