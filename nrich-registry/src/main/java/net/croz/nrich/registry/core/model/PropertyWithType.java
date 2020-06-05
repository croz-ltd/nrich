package net.croz.nrich.registry.core.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PropertyWithType {

    private final String name;

    private final Class<?> type;

}
