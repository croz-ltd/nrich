package net.croz.nrich.registry.configuration.model;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RegistryField {

    private final String name;

    private final String javascriptType;

    private final String originalType;

    private final boolean isOneToOne;

    private final String oneToOneReferencedClass;

    private final FormFieldDisplayConfiguration formFieldDisplayConfiguration;

    private final ColumnFieldDisplayConfiguration columnFieldDisplayConfiguration;

}
