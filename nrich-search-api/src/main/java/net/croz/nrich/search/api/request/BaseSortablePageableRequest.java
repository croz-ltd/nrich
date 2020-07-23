package net.croz.nrich.search.api.request;

import lombok.Getter;
import lombok.Setter;
import net.croz.nrich.search.api.model.sort.SortProperty;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Helper class for creating request that support paging and sorting.
 */
@Setter
@Getter
public abstract class BaseSortablePageableRequest implements SortablePageableRequest {

    /**
     * Page number.
     */
    @Min(0)
    @NotNull
    private Integer pageNumber;

    /**
     * Number of elements to fetch.
     */
    @Min(1)
    @NotNull
    private Integer pageSize;

    /**
     * List of properties to sort by.
     */
    private List<SortProperty> sortPropertyList;

}
