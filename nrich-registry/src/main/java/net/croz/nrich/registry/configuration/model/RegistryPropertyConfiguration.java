package net.croz.nrich.registry.configuration.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RegistryPropertyConfiguration {

    private final String name;

    private final JavascriptType javascriptType;

    private final String originalType;

    private final boolean isId;

    private final boolean isDecimal;

    private final boolean isSingularAssociation;

    private final String singularAssociationReferencedClass;

    private final String formLabel;

    private final String columnHeader;

    private final boolean editable;

    private final boolean sortable;

}
