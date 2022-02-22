package net.croz.nrich.registry.configuration.comparator;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class DisplayOrderComparator {

    protected final List<String> propertyDisplayOrderList;

    public int comparePropertiesByDisplayList(String firstPropertyName, String secondPropertyName) {
        if (!propertyDisplayOrderList.contains(firstPropertyName)) {
            return 1;
        }

        if (!propertyDisplayOrderList.contains(secondPropertyName)) {
            return -1;
        }

        Integer firstPropertyIndex = propertyDisplayOrderList.indexOf(firstPropertyName);
        Integer secondPropertyIndex = propertyDisplayOrderList.indexOf(secondPropertyName);

        return firstPropertyIndex.compareTo(secondPropertyIndex);
    }
}
