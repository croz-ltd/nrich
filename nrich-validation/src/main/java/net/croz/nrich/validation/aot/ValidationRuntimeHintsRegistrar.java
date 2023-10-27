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

package net.croz.nrich.validation.aot;

import net.croz.nrich.validation.constraint.mapping.DefaultConstraintMappingContributor;
import net.croz.nrich.validation.constraint.validator.LastTimestampInDayValidator;
import net.croz.nrich.validation.constraint.validator.MinDateValidator;
import net.croz.nrich.validation.constraint.validator.SpelExpressionValidator;
import net.croz.nrich.validation.constraint.validator.InListValidator;
import net.croz.nrich.validation.constraint.validator.MaxSizeInBytesValidator;
import net.croz.nrich.validation.constraint.validator.NotNullWhenValidator;
import net.croz.nrich.validation.constraint.validator.NullWhenValidator;
import net.croz.nrich.validation.constraint.validator.ValidFileResolvableValidator;
import net.croz.nrich.validation.constraint.validator.ValidFileValidator;
import net.croz.nrich.validation.constraint.validator.ValidOibValidator;
import net.croz.nrich.validation.constraint.validator.ValidRangeValidator;
import net.croz.nrich.validation.constraint.validator.ValidSearchPropertiesValidator;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.TypeReference;

import java.util.Collections;
import java.util.List;

public class ValidationRuntimeHintsRegistrar implements RuntimeHintsRegistrar {

    public static final String RESOURCE_BUNDLE = "nrich-validation-messages";

    public static final List<String> RESOURCE_PATTERN_LIST = List.of("META-INF/validation.xml", "META-INF/validation-configuration-1.1.xsd");

    public static final List<TypeReference> TYPE_REFERENCE_LIST = Collections.unmodifiableList(TypeReference.listOf(
        DefaultConstraintMappingContributor.class, ValidOibValidator.class, ValidSearchPropertiesValidator.class, ValidRangeValidator.class, MaxSizeInBytesValidator.class,
        NotNullWhenValidator.class, NullWhenValidator.class, ValidFileValidator.class, ValidFileResolvableValidator.class, InListValidator.class, SpelExpressionValidator.class,
        MinDateValidator.class, LastTimestampInDayValidator.class
    ));

    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        RESOURCE_PATTERN_LIST.forEach(hints.resources()::registerPattern);

        hints.resources().registerResourceBundle(RESOURCE_BUNDLE);

        hints.reflection().registerTypes(TYPE_REFERENCE_LIST, hint -> hint.withMembers(MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS));
    }
}
