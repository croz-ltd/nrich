package net.croz.nrich.registry.configuration.comparator;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class DisplayOrderComparatorTest {

    private final DisplayOrderComparator displayOrderComparator = new DisplayOrderComparator(Arrays.asList("first", "second"));

    @CsvSource({ "first,second,-1", "third,second,1", "first,third,-1" })
    @ParameterizedTest
    void shouldReturnCorrectOrder(String firstProperty, String secondProperty, int expectedResult) {
        // expect
        assertThat(displayOrderComparator.comparePropertiesByDisplayList(firstProperty, secondProperty)).isEqualTo(expectedResult);
    }
}
