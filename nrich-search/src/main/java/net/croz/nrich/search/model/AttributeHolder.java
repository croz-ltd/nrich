package net.croz.nrich.search.model;

import lombok.Value;

import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.ManagedType;

@Value
public class AttributeHolder {

    Attribute<?, ?> attribute;

    ManagedType<?> managedType;

    boolean isPlural;

}
