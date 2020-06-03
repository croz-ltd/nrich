package net.croz.nrich.registry.history.request;

import lombok.Data;
import net.croz.nrich.registry.core.model.SearchParameter;
import net.croz.nrich.search.api.model.SortProperty;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class ListRegistryHistoryRequest {

    @NotNull
    private String registryId;

    @Min(0)
    @NotNull
    private Integer pageNumber;

    @Max(1000)
    @NotNull
    private Integer pageSize;

    private Object registryRecordId;

    private SearchParameter searchParameter;

    private List<SortProperty> sortPropertyList;

}
