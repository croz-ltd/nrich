package net.croz.nrich.registry.configuration.comparator;

import net.croz.nrich.registry.api.configuration.model.property.RegistryPropertyConfiguration;
import org.springframework.util.CollectionUtils;

import java.util.Comparator;
import java.util.List;

public class RegistryPropertyComparator extends DisplayOrderComparator implements Comparator<RegistryPropertyConfiguration> {

    public RegistryPropertyComparator(List<String> propertyDisplayOrderList) {
        super(propertyDisplayOrderList);
    }

    @Override
    public int compare(RegistryPropertyConfiguration firstProperty, RegistryPropertyConfiguration secondProperty) {
        String firstPropertyName = firstProperty.getName();
        String secondPropertyName = secondProperty.getName();

        if (CollectionUtils.isEmpty(propertyDisplayOrderList)) {
            if (firstProperty.isId()) {
                return -1;
            }
            if (secondProperty.isId()) {
                return 1;
            }

            return firstPropertyName.compareTo(secondPropertyName);
        }

        return comparePropertiesByDisplayList(firstPropertyName, secondPropertyName);
    }
}
