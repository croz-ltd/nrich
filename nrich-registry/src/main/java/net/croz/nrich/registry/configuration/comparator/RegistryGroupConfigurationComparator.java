package net.croz.nrich.registry.configuration.comparator;

import net.croz.nrich.registry.api.configuration.model.RegistryCategoryConfiguration;
import org.springframework.util.CollectionUtils;

import java.util.Comparator;
import java.util.List;

public class RegistryGroupConfigurationComparator extends DisplayOrderComparator implements Comparator<RegistryCategoryConfiguration> {

    public RegistryGroupConfigurationComparator(final List<String> propertyDisplayOrderList) {
        super(propertyDisplayOrderList);
    }

    @Override
    public int compare(final RegistryCategoryConfiguration firstConfiguration, final RegistryCategoryConfiguration secondConfiguration) {
        final String firstGroupId = firstConfiguration.getRegistryCategoryId();
        final String secondGroupId = secondConfiguration.getRegistryCategoryId();

        if (CollectionUtils.isEmpty(propertyDisplayOrderList)) {
            return firstGroupId.compareTo(secondGroupId);
        }

        return comparePropertiesByDisplayList(firstGroupId, secondGroupId);
    }
}
