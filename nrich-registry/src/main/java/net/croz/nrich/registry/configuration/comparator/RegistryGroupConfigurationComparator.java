package net.croz.nrich.registry.configuration.comparator;

import lombok.RequiredArgsConstructor;
import net.croz.nrich.registry.configuration.model.RegistryGroupConfiguration;
import org.springframework.util.CollectionUtils;

import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor
public class RegistryGroupConfigurationComparator implements Comparator<RegistryGroupConfiguration> {

    private final List<String> groupDisplayOrderList;

    @Override
    public int compare(final RegistryGroupConfiguration firstConfiguration, final RegistryGroupConfiguration secondConfiguration) {
        final String firstGroupId = firstConfiguration.getRegistryGroupId();
        final String secondGroupId = secondConfiguration.getRegistryGroupId();

        if (CollectionUtils.isEmpty(groupDisplayOrderList)) {
            return firstGroupId.compareTo(secondGroupId);
        }

        return DisplayOrderComparatorUtil.comparePropertiesByDisplayList(groupDisplayOrderList, firstGroupId, secondGroupId);
    }
}
