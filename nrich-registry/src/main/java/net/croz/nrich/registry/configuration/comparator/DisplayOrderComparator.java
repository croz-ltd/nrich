package net.croz.nrich.registry.configuration.comparator;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.util.List;

@RequiredArgsConstructor
@Getter
public class DisplayOrderComparator implements Serializable {

    private static final long serialVersionUID = 4980226671627040327L;

    private final List<String> propertyDisplayOrderList;

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
