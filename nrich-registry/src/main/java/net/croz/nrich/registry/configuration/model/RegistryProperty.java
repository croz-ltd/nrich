package net.croz.nrich.registry.configuration.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RegistryProperty {

    private final String name;

    private final JavascriptType javascriptType;

    private final String originalType;

    private final boolean isDecimal;

    private final boolean isOneToOne;

    private final String oneToOneReferencedClass;

    private final FormPropertyDisplayConfiguration formPropertyDisplayConfiguration;

    private final ColumnPropertyDisplayConfiguration columnPropertyDisplayConfiguration;

}
