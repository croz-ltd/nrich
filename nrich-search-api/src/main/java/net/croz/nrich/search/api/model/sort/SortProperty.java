package net.croz.nrich.search.api.model.sort;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * Combination of property to sort by and sort direction.
 */
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class SortProperty {

    /**
     * Property to sort by.
     */
    @NotNull
    private String property;

    /**
     * Sort direction.
     */
    @NotNull
    private SortDirection direction;

}
