package net.croz.nrich.registry.configuration.comparator;

import java.util.List;

public final class DisplayOrderComparatorUtil {

    private DisplayOrderComparatorUtil() {
    }

    public static int comparePropertiesByDisplayList(final List<String> propertyDisplayOrderList, final String firstPropertyName, final String secondPropertyName) {
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
