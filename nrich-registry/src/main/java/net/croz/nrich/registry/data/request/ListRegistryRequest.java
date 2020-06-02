package net.croz.nrich.registry.data.request;

import lombok.Data;
import net.croz.nrich.registry.data.model.SearchParameter;
import net.croz.nrich.search.api.model.SortProperty;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
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
