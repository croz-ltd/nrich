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

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.util.List;

/**
 * @param controllerAdviceEnabled                       Whether {@link net.croz.nrich.webmvc.advice.NotificationErrorHandlingRestControllerAdvice} controller advice is enabled.
 * @param exceptionAuxiliaryDataResolvingEnabled        Whether default {@link net.croz.nrich.webmvc.api.service.ExceptionAuxiliaryDataResolverService} is enabled.
 * @param convertEmptyStringsToNull                     Whether empty strings should be converted to null when binding requests.
 * @param ignoreTransientFields                         Whether transient fields should be ignored when binding requests.
 * @param exceptionToUnwrapList                         List of exceptions that will be unwrapping their cause.
 * @param exceptionAuxiliaryDataToIncludeInNotification List of exception auxiliary data to be included in notification sent to client.
 * @param defaultLocale                                 Optional property. Default locale.
 * @param allowedLocaleList                             Optional property. List of locales users can set.
 */
@ConfigurationProperties("nrich.webmvc")
public record NrichWebMvcProperties(@DefaultValue("true") boolean controllerAdviceEnabled, @DefaultValue("true") boolean exceptionAuxiliaryDataResolvingEnabled,
                                    @DefaultValue("true") boolean convertEmptyStringsToNull, @DefaultValue("true") boolean ignoreTransientFields,
                                    @DefaultValue("java.util.concurrent.ExecutionException") List<String> exceptionToUnwrapList,
                                    @DefaultValue("uuid") List<String> exceptionAuxiliaryDataToIncludeInNotification, String defaultLocale, List<String> allowedLocaleList) {

}
