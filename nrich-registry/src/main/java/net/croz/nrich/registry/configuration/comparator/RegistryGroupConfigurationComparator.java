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

        if (!groupDisplayOrderList.contains(firstGroupId)) {
            return 1;
        }

        if (!groupDisplayOrderList.contains(secondGroupId)) {
            return -1;
        }

        final Integer firstGroupIndex = groupDisplayOrderList.indexOf(firstGroupId);
        final Integer secondGroupIndex = groupDisplayOrderList.indexOf(secondGroupId);

        return firstGroupIndex.compareTo(secondGroupIndex);
    }
}
