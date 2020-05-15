package net.croz.nrich.search.api.model;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class SortProperty {

    @NotNull
    private String name;

    @NotNull
    private SortDirection direction;

}
