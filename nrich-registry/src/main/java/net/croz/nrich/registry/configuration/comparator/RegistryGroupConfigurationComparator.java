package net.croz.nrich.registry.configuration.comparator;

import net.croz.nrich.registry.configuration.model.RegistryGroupConfiguration;
import org.springframework.util.CollectionUtils;

import java.util.Comparator;
import java.util.List;

public class RegistryGroupConfigurationComparator extends DisplayOrderComparator implements Comparator<RegistryGroupConfiguration> {

    public RegistryGroupConfigurationComparator(final List<String> propertyDisplayOrderList) {
        super(propertyDisplayOrderList);
    }

    @Override
    public int compare(final RegistryGroupConfiguration firstConfiguration, final RegistryGroupConfiguration secondConfiguration) {
        final String firstGroupId = firstConfiguration.getRegistryGroupId();
        final String secondGroupId = secondConfiguration.getRegistryGroupId();

        if (CollectionUtils.isEmpty(propertyDisplayOrderList)) {
            return firstGroupId.compareTo(secondGroupId);
        }

        return comparePropertiesByDisplayList(firstGroupId, secondGroupId);
    }
}
