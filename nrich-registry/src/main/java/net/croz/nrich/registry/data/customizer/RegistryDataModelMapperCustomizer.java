package net.croz.nrich.registry.data.customizer;

import lombok.RequiredArgsConstructor;
import net.croz.nrich.registry.api.core.customizer.ModelMapperCustomizer;
import net.croz.nrich.registry.api.core.customizer.ModelMapperType;
import net.croz.nrich.registry.api.core.model.RegistryOverrideConfiguration;
import net.croz.nrich.registry.api.core.model.RegistryOverrideConfigurationHolder;
import org.modelmapper.ModelMapper;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class RegistryDataModelMapperCustomizer implements ModelMapperCustomizer {

    private final List<RegistryOverrideConfigurationHolder> registryOverrideConfigurationHolderList;

    @Override
    public void customize(ModelMapperType type, ModelMapper modelMapper) {
        if (type != ModelMapperType.DATA || CollectionUtils.isEmpty(registryOverrideConfigurationHolderList)) {
            return;
        }

        registryOverrideConfigurationHolderList.forEach(registryOverrideConfigurationHolder -> {
            List<String> ignoredPropertyList = Optional.ofNullable(registryOverrideConfigurationHolder.getOverrideConfiguration())
                .map(RegistryOverrideConfiguration::getIgnoredPropertyList)
                .orElse(Collections.emptyList());

            if (ignoredPropertyList.isEmpty()) {
                return;
            }

            modelMapper.getConfiguration().setPropertyCondition(context -> {
                if (context.getParent() != null && registryOverrideConfigurationHolder.getType().equals(context.getParent().getDestinationType())) {
                    return context.getMapping().getDestinationProperties().stream().noneMatch(property -> ignoredPropertyList.contains(property.getName()));
                }

                return true;
            });
        });
    }
}
