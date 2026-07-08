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

package net.croz.nrich.problemdetail.aot;

import net.croz.nrich.problemdetail.api.model.ValidationError;
import net.croz.nrich.problemdetail.constant.ProblemDetailConstants;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;

/**
 * Registers reflection hints for {@link ValidationError} so the record serializes correctly when
 * the host application is built as a GraalVM native image. The record is stashed as an opaque value
 * inside a {@link org.springframework.http.ProblemDetail} extension property (a {@code Map} value),
 * which Spring Boot's AOT return-type walker cannot discover on its own.
 */
public class ProblemDetailRuntimeHintsRegistrar implements RuntimeHintsRegistrar {

    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        hints.reflection().registerType(ValidationError.class, MemberCategory.INVOKE_DECLARED_CONSTRUCTORS, MemberCategory.INVOKE_PUBLIC_METHODS);
        hints.resources().registerResourceBundle(ProblemDetailConstants.MESSAGES_RESOURCE_BUNDLE);
    }

}
