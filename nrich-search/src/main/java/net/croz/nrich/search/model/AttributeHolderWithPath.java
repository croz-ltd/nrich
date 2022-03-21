package net.croz.nrich.search.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class AttributeHolderWithPath {

    private final String path;

    private final AttributeHolder attributeHolder;

    public boolean isFound() {
        return attributeHolder.isFound();
    }

    public static AttributeHolderWithPath notFound() {
        return new AttributeHolderWithPath(null, AttributeHolder.notFound());
    }
}
