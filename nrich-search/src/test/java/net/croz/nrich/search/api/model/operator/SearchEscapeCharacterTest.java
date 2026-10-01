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

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SearchEscapeCharacterTest {

    @Test
    void shouldReturnNullWhenEscapingNullValue() {
        // when
        String result = SearchEscapeCharacter.DEFAULT.escape(null);

        // then
        assertThat(result).isNull();
    }

    @Test
    void shouldReturnInputUnchangedWhenItContainsNoSpecialCharacters() {
        // given
        String input = "plain value";

        // when
        String result = SearchEscapeCharacter.DEFAULT.escape(input);

        // then
        assertThat(result).isEqualTo(input);
    }

    @Test
    void shouldEscapePercentCharacterWithDefaultEscapeCharacter() {
        // when
        String result = SearchEscapeCharacter.DEFAULT.escape("50%");

        // then
        assertThat(result).isEqualTo("50\\%");
    }

    @Test
    void shouldEscapeUnderscoreCharacterWithDefaultEscapeCharacter() {
        // when
        String result = SearchEscapeCharacter.DEFAULT.escape("a_b");

        // then
        assertThat(result).isEqualTo("a\\_b");
    }

    @Test
    void shouldEscapeEscapeCharacterItself() {
        // when
        String result = SearchEscapeCharacter.DEFAULT.escape("a\\b");

        // then
        assertThat(result).isEqualTo("a\\\\b");
    }

    @Test
    void shouldEscapeAllSpecialCharactersInSameValue() {
        // when
        String result = SearchEscapeCharacter.DEFAULT.escape("a%b_c\\d");

        // then
        assertThat(result).isEqualTo("a\\%b\\_c\\\\d");
    }

    @Test
    void shouldUseConfiguredEscapeCharacter() {
        // given
        SearchEscapeCharacter escapeCharacter = new SearchEscapeCharacter('|');

        // when
        String result = escapeCharacter.escape("a%b|c");

        // then
        assertThat(result).isEqualTo("a|%b||c");
    }

    @Test
    void shouldExposeConfiguredEscapeCharacterValue() {
        // when
        SearchEscapeCharacter escapeCharacter = new SearchEscapeCharacter('|');

        // then
        assertThat(escapeCharacter.value()).isEqualTo('|');
    }

    @Test
    void shouldExposeDefaultEscapeCharacterValue() {
        // expect
        assertThat(SearchEscapeCharacter.DEFAULT.value()).isEqualTo('\\');
    }
}
