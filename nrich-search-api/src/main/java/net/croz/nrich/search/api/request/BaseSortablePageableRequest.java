package net.croz.nrich.search.api.request;

import net.croz.nrich.search.api.model.SortProperty;

import java.util.List;

public interface BaseSortablePageableRequest {

    Integer getPageNumber();

    Integer getPageSize();

    List<SortProperty> getSortFieldList();

}
