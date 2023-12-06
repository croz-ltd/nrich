/*
 * Copyright 2020-2023 CROZ d.o.o, the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License");
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

package net.croz.nrich.search.util;

import net.croz.nrich.search.util.stub.OnePropertyClass;
import net.croz.nrich.search.util.stub.ThreePropertiesClass;
import net.croz.nrich.search.util.stub.TwoPropertiesClass;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class FieldExtractionUtilTest {

    @MethodSource("shouldExtractCorrectNumberOfFieldsForSubclassesMethodSource")
    @ParameterizedTest
    void shouldExtractCorrectNumberOfFieldsForSubclasses(Class<?> givenType, int numberOfExpectedFields) {
        // when
        List<Field> classFields = FieldExtractionUtil.getAllFields(givenType);

        // then
        assertThat(classFields).hasSize(numberOfExpectedFields);
    }

    private static Stream<Arguments> shouldExtractCorrectNumberOfFieldsForSubclassesMethodSource() {
        return Stream.of(
            Arguments.of(OnePropertyClass.class, 1),
            Arguments.of(TwoPropertiesClass.class, 2),
            Arguments.of(ThreePropertiesClass.class, 3)
        );
    }
}

