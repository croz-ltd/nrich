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
