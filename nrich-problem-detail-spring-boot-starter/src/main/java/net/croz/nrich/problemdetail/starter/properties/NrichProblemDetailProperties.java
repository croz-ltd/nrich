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

package net.croz.nrich.problemdetail.starter.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.util.List;

/**
 * @param enabled                           Whether the nrich ProblemDetail error-handling advice is registered. Defaults to {@code true} - the module is on-by-default once added as a dependency.
 * @param loggingServiceRegistrationEnabled Whether nrich registers a {@link net.croz.nrich.logging.api.service.LoggingService} configured to also resolve the {@code problemDetail.nrich.logging.*} keys. Defaults to {@code true}; set to {@code false} to fall back to the default logging service (legacy keys only).
 * @param registerMessages                  Whether nrich registers its {@code nrich-problem-detail-messages} bundle (default detail + Spring validation-exception detail overrides, with {@code _hr}) into the application {@link org.springframework.context.MessageSource}. Defaults to {@code true}.
 * @param exceptionToUnwrapList             Fully-qualified names of wrapper exceptions whose {@link Throwable#getCause() cause} should be unwrapped before routing. Defaults to {@code java.util.concurrent.ExecutionException}.
 * @param contributor                       Per-contributor on/off flags. Each defaults to {@code true} so all default nrich extensions ship out of the box; set any to {@code false} to drop that field from the wire.
 * @param includeRejectedValue              Whether the rejected value is included in each {@code errors[]} entry. Defaults to {@code false} so submitted values (passwords, PII) are not echoed back; mirrors Spring's {@code server.error.include-binding-errors}.
 * @param fallbackToClassName               Whether the {@code code} extension falls back to the exception's fully-qualified class name when no {@code problemDetail.nrich.code.<fqcn>} message and no {@link net.croz.nrich.core.api.exception.ExceptionWithMessageCode} apply. Defaults to {@code false} so internal class names are not exposed to clients.
 */
@ConfigurationProperties("nrich.problem-detail")
public record NrichProblemDetailProperties(@DefaultValue("true") boolean enabled, @DefaultValue("true") boolean loggingServiceRegistrationEnabled,
                                           @DefaultValue("true") boolean registerMessages,
                                           @DefaultValue("java.util.concurrent.ExecutionException") List<String> exceptionToUnwrapList,
                                           @DefaultValue Contributor contributor, @DefaultValue("false") boolean includeRejectedValue,
                                           @DefaultValue("false") boolean fallbackToClassName) {

    /**
     * @param errors    Validation {@code errors[]} contributor.
     * @param code      {@code code} extension contributor.
     * @param severity  {@code severity} extension contributor.
     * @param errorId   {@code errorId} extension contributor.
     * @param timestamp {@code timestamp} extension contributor.
     */
    public record Contributor(@DefaultValue("true") boolean errors, @DefaultValue("true") boolean code, @DefaultValue("true") boolean severity,
                              @DefaultValue("true") boolean errorId, @DefaultValue("true") boolean timestamp) {

    }

}
