package net.croz.nrich.search.api.request;

import lombok.Getter;
import lombok.Setter;
import net.croz.nrich.search.api.model.sort.SortProperty;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Setter
@Getter
public abstract class BaseSortablePageableRequest implements SortablePageableRequest {

    @Min(1)
    @NotNull
    private Integer pageSize;

    @Min(0)
    @NotNull
    private Integer pageNumber;

    private List<SortProperty> sortPropertyList;

}
