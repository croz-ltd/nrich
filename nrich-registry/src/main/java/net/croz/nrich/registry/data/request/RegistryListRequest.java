package net.croz.nrich.registry.data.request;

import lombok.Data;
import net.croz.nrich.registry.data.model.SearchParameter;
import net.croz.nrich.search.api.model.SortProperty;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class RegistryListRequest {

    @NotNull
    private String classFullName;

    @NotNull
    private Integer pageNumber;

    @NotNull
    private Integer pageSize;

    @NotNull
    private SearchParameter searchParameter;

    private List<SortProperty> sortPropertyList;

}
