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
        RegistryPropertyComparator registryPropertyComparator = new RegistryPropertyComparator(null);

        // when
        Integer result = registryPropertyComparator.compare(RegistryPropertyConfiguration.builder().isId(isFirstId).name("third").build(), RegistryPropertyConfiguration.builder().isId(isSecondId).name("second").build());

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
