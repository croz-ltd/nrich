package net.croz.nrich.registry.api.data.request;

import lombok.Getter;
import lombok.Setter;
import net.croz.nrich.search.api.model.sort.SortProperty;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Setter
@Getter
public class ListRegistryRequest {

    @NotNull
    private String classFullName;

    @Min(0)
    @NotNull
    private Integer pageNumber;

    @Max(1000)
    @NotNull
    private Integer pageSize;

    private SearchParameter searchParameter;

    private List<SortProperty> sortPropertyList;

}
