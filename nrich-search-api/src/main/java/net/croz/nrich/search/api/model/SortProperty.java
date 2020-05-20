package net.croz.nrich.search.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class SortProperty {

    @NotNull
    private String name;

    @NotNull
    private SortDirection direction;

}
