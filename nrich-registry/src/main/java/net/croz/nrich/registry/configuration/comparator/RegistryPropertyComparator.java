package net.croz.nrich.registry.configuration.comparator;

import net.croz.nrich.registry.configuration.model.RegistryProperty;
import org.springframework.util.CollectionUtils;

import java.util.Comparator;
import java.util.List;

public class RegistryPropertyComparator extends DisplayOrderComparator implements Comparator<RegistryProperty> {

    public RegistryPropertyComparator(final List<String> propertyDisplayOrderList) {
        super(propertyDisplayOrderList);
    }

    @Override
    public int compare(final RegistryProperty firstProperty, final RegistryProperty secondProperty) {
        final String firstPropertyName = firstProperty.getName();
        final String secondPropertyName = secondProperty.getName();

        if (CollectionUtils.isEmpty(getPropertyDisplayOrderList())) {
            if (firstProperty.isId()) {
                return  -1;
            }
            if (secondProperty.isId()) {
                return 1;
            }

            return firstPropertyName.compareTo(secondPropertyName);
        }

        return comparePropertiesByDisplayList(firstPropertyName, secondPropertyName);
    }
}
