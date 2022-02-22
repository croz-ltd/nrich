package net.croz.nrich.registry.api.configuration.model.property;

import lombok.Builder;
import lombok.Getter;

/**
 * Represents client property configuration that can be used when building form and grids on client side.
 */
@Getter
@Builder
public class RegistryPropertyConfiguration {

    /**
     * Property name.
     */
    private final String name;

    /**
     * Property JavascriptType (converted from property type).
     */
    private final JavascriptType javascriptType;

    /**
     * Property original type class name
     */
    private final String originalType;

    /**
     * Whether this property is id.
     */
    private final boolean isId;

    /**
     * Whether this property is decimal (Javascript has no specific decimal type).
     */
    private final boolean isDecimal;

    /**
     * Whether this property represents a singular association (JPA @OneToOne or @ManyToOne)
     */
    private final boolean isSingularAssociation;

    /**
     * Singular association class
     */
    private final String singularAssociationReferencedClass;

    /**
     * Label for this property in form view.
     */
    private final String formLabel;

    /**
     * Header for this property in grid view.
     */
    private final String columnHeader;

    /**
     * Whether this property is editable.
     */
    private final boolean editable;

    /**
     * Whether this property is sortable.
     */
    private final boolean sortable;

    /**
     * Whether this property is searchable.
     */
    private final boolean searchable;

}
