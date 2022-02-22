package net.croz.nrich.registry.configuration.comparator;

import net.croz.nrich.registry.api.configuration.model.RegistryGroupConfiguration;
import org.springframework.util.CollectionUtils;

import java.util.Comparator;
import java.util.List;

public class RegistryGroupConfigurationComparator extends DisplayOrderComparator implements Comparator<RegistryGroupConfiguration> {

    public RegistryGroupConfigurationComparator(List<String> propertyDisplayOrderList) {
        super(propertyDisplayOrderList);
    }

    @Override
    public int compare(RegistryGroupConfiguration firstConfiguration, RegistryGroupConfiguration secondConfiguration) {
        String firstGroupId = firstConfiguration.getGroupId();
        String secondGroupId = secondConfiguration.getGroupId();

        if (CollectionUtils.isEmpty(propertyDisplayOrderList)) {
            return firstGroupId.compareTo(secondGroupId);
        }

        return comparePropertiesByDisplayList(firstGroupId, secondGroupId);
    }
}
