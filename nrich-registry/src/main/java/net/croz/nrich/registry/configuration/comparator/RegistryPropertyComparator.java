package net.croz.nrich.registry.configuration.comparator;

import lombok.RequiredArgsConstructor;
import net.croz.nrich.registry.configuration.model.RegistryProperty;
import org.springframework.util.CollectionUtils;

import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor
public class RegistryPropertyComparator implements Comparator<RegistryProperty> {

    private final List<String> propertyDisplayOrderList;

    @Override
    public int compare(final RegistryProperty firstProperty, final RegistryProperty secondProperty) {
        final String firstPropertyName = firstProperty.getName();
        final String secondPropertyName = secondProperty.getName();

        if (CollectionUtils.isEmpty(propertyDisplayOrderList)) {
            if (firstProperty.isId()) {
                return  -1;
            }
            if (secondProperty.isId()) {
                return 1;
            }

            return firstPropertyName.compareTo(secondPropertyName);
        }

        return DisplayOrderComparatorUtil.comparePropertiesByDisplayList(propertyDisplayOrderList, firstPropertyName, secondPropertyName);
    }
}
