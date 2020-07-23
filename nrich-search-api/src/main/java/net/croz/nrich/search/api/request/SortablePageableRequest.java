package net.croz.nrich.search.api.request;

import net.croz.nrich.search.api.model.sort.SortProperty;

import java.util.List;

/**
 * Helper interface for creating request that support paging and sorting.
 */
public interface SortablePageableRequest {

    /**
     * Page number.
     */
    Integer getPageNumber();

    /**
     * Number of elements to fetch.
     */
    Integer getPageSize();

    /**
     * List of properties to sort by.
     */
    List<SortProperty> getSortPropertyList();

}
