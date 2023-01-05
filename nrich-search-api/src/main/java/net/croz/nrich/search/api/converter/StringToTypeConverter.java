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

package net.croz.nrich.search.api.converter;

/**
 * Converts string to required type.
 *
 * @param <T> type for conversion
 */
public interface StringToTypeConverter<T> {

    /**
     * Converts string value to required type.
     *
     * @param value        value to convert
     * @param requiredType type to convert to
     * @return converted value or null if conversion failed
     */
    T convert(String value, Class<?> requiredType);

    /**
     * Whether this converter supports conversion.
     *
     * @param requiredType type to convert to
     * @return whether this converter supports conversion
     */
    boolean supports(Class<?> requiredType);

}
