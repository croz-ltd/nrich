package net.croz.nrich.registry.configuration.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ColumnPropertyDisplayConfiguration {

    private final String header;

    private final boolean searchable;

    private final boolean sortable;

}
