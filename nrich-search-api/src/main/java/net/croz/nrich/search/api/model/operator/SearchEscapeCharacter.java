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

package net.croz.nrich.search.api.model.operator;

/**
 * Character used to escape wildcard characters ({@code %} and {@code _}) in {@code LIKE} expressions built by wildcard-aware {@link SearchOperator} implementations.
 * Similar to Spring Data JPA's {@code EscapeCharacter} without adding a dependency on it from the api module.
 *
 * @param value escape character
 */
public record SearchEscapeCharacter(char value) {

    public static final SearchEscapeCharacter DEFAULT = new SearchEscapeCharacter('\\');

    /**
     * Escapes {@code %}, {@code _} and the escape character itself in the supplied value.
     *
     * @param input value to escape, can be {@code null}
     * @return escaped value or {@code null} when input is {@code null}
     */
    public String escape(String input) {
        if (input == null) {
            return null;
        }

        StringBuilder result = new StringBuilder(input.length() + 4);

        for (int i = 0; i < input.length(); i++) {
            char current = input.charAt(i);

            if (current == '%' || current == '_' || current == value) {
                result.append(value);
            }

            result.append(current);
        }

        return result.toString();
    }
}
