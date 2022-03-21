package net.croz.nrich.search.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.ManagedType;

@RequiredArgsConstructor
@Getter
public class AttributeHolder {

    private final Attribute<?, ?> attribute;

    private final ManagedType<?> managedType;

    private final boolean isPlural;

    public boolean isFound() {
        return attribute != null;
    }

    public static AttributeHolder notFound() {
        return new AttributeHolder(null, null, false);
    }
}
