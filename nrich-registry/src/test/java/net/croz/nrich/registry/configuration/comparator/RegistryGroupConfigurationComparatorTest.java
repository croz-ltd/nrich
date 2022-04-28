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

import net.croz.nrich.registry.api.configuration.model.RegistryGroupConfiguration;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class RegistryGroupConfigurationComparatorTest {

    @Test
    void shouldCompareByGroupIdWhenPropertiesAreEmpty() {
        // given
        RegistryGroupConfiguration firstGroup = new RegistryGroupConfiguration("first", "first.display", Collections.emptyList());
        RegistryGroupConfiguration secondGroup = new RegistryGroupConfiguration("second", "second.display", Collections.emptyList());
        RegistryGroupConfigurationComparator comparator = new RegistryGroupConfigurationComparator(null);

        // when
        Integer result = comparator.compare(firstGroup, secondGroup);

        // then
        assertThat(result).isNegative();
    }

    @Test
    void shouldCompareByDisplayOrderWhenItExists() {
        // given
        RegistryGroupConfiguration firstGroup = new RegistryGroupConfiguration("first", "2.display", Collections.emptyList());
        RegistryGroupConfiguration secondGroup = new RegistryGroupConfiguration("second", "1.display", Collections.emptyList());
        RegistryGroupConfigurationComparator comparator = new RegistryGroupConfigurationComparator(Arrays.asList("1.display", "2.display"));

        // when
        Integer result = comparator.compare(firstGroup, secondGroup);

        // then
        assertThat(result).isPositive();
    }
}
