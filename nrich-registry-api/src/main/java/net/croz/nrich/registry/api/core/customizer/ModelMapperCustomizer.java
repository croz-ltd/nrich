package net.croz.nrich.registry.api.core.customizer;

import org.modelmapper.ModelMapper;

public interface ModelMapperCustomizer {

    void customize(ModelMapperType type, ModelMapper modelMapper);

}
