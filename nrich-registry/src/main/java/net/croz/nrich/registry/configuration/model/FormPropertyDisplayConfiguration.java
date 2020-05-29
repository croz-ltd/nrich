package net.croz.nrich.registry.configuration.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FormPropertyDisplayConfiguration {

    private final String label;

    private final boolean editable;

    private final String componentType;

}
