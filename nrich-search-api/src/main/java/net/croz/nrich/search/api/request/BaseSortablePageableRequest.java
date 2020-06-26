package net.croz.nrich.search.api.request;

import lombok.Getter;
import lombok.Setter;
import net.croz.nrich.search.api.model.sort.SortProperty;

import java.util.List;

@Setter
@Getter
public abstract class BaseSortablePageableRequest implements SortablePageableRequest {

    private Integer pageSize;

    private Integer pageNumber;

    private List<SortProperty> sortPropertyList;

}
