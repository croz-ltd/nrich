package net.croz.nrich.registry.configuration.comparator;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class DisplayOrderComparator {

    private final List<String> propertyDisplayOrderList;

    public int comparePropertiesByDisplayList(final String firstPropertyName, final String secondPropertyName) {
        if (!propertyDisplayOrderList.contains(firstPropertyName)) {
            return 1;
        }

        if (!propertyDisplayOrderList.contains(secondPropertyName)) {
            return -1;
        }

        final Integer firstPropertyIndex = propertyDisplayOrderList.indexOf(firstPropertyName);
        final Integer secondPropertyIndex = propertyDisplayOrderList.indexOf(secondPropertyName);

        return firstPropertyIndex.compareTo(secondPropertyIndex);
    }
}
