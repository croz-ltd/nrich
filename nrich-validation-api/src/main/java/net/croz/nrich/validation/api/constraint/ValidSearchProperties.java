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

package net.croz.nrich.validation.api.constraint;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * At least one group of annotated element must contain all properties that are not null.
 */
@SuppressWarnings("unused")
@Target({ ANNOTATION_TYPE, TYPE_USE, TYPE })
@Retention(RUNTIME)
@Repeatable(ValidSearchProperties.List.class)
@Documented
@Constraint(validatedBy = {})
public @interface ValidSearchProperties {

    String message() default "{nrich.constraint.searchFields.invalid.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * List of property groups.
     *
     * @return group of properties.
     * @see PropertyGroup
     */
    PropertyGroup[] propertyGroup();

    /**
     * Holder for a list of property names
     */
    @Target(ANNOTATION_TYPE)
    @Retention(RUNTIME)
    @Documented
    @interface PropertyGroup {

        String[] value();
    }

    /**
     * Defines several {@link ValidSearchProperties} annotations on the same element.
     *
     * @see ValidSearchProperties
     */
    @Target({ ANNOTATION_TYPE, TYPE_USE, TYPE })
    @Retention(RUNTIME)
    @Documented
    @interface List {

        ValidSearchProperties[] value();
    }
}
