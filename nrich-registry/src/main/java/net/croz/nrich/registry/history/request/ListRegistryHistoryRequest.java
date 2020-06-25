package net.croz.nrich.registry.history.request;

import lombok.Getter;
import lombok.Setter;
import net.croz.nrich.search.api.model.sort.SortProperty;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Setter
@Getter
public class ListRegistryHistoryRequest {

    @NotNull
    private String registryId;

    @Min(0)
    @NotNull
    private Integer pageNumber;

    @Max(100)
    @NotNull
    private Integer pageSize;

    private Object registryRecordId;

    private List<SortProperty> sortPropertyList;

}
