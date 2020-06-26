package net.croz.nrich.search.api.model.sort;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class SortProperty {

    @NotNull
    private String property;

    @NotNull
    private SortDirection direction;

}
