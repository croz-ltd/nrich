package net.croz.nrich.search.api.request;

import net.croz.nrich.search.api.model.sort.SortProperty;

import java.util.List;

public interface SortablePageableRequest {

    Integer getPageNumber();

    Integer getPageSize();

    List<SortProperty> getSortPropertyList();

}
