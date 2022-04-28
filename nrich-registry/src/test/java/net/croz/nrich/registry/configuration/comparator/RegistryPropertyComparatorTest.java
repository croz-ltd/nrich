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

package net.croz.nrich.registry.configuration.comparator;

import net.croz.nrich.registry.api.configuration.model.property.RegistryPropertyConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class RegistryPropertyComparatorTest {

    @CsvSource({ "true,false,-1", "false,true,1", "false,false,1" })
    @ParameterizedTest
    void shouldCompareByPropertyNameWhenDisplayOrderIsEmpty(boolean isFirstId, boolean isSecondId, int expectedResult) {
        // given
        RegistryPropertyConfiguration first = RegistryPropertyConfiguration.builder().isId(isFirstId).name("third").build();
        RegistryPropertyConfiguration second = RegistryPropertyConfiguration.builder().isId(isSecondId).name("second").build();
        RegistryPropertyComparator registryPropertyComparator = new RegistryPropertyComparator(null);

        // when
        Integer result = registryPropertyComparator.compare(first, second);

        // then
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void shouldCompareByDisplayOrderWhenPresent() {
        // given
        RegistryPropertyComparator registryPropertyComparator = new RegistryPropertyComparator(Arrays.asList("first", "second"));

        // when
        Integer result = registryPropertyComparator.compare(RegistryPropertyConfiguration.builder().name("first").build(), RegistryPropertyConfiguration.builder().name("second").build());

        // then
        assertThat(result).isEqualTo(-1);
    }
}
