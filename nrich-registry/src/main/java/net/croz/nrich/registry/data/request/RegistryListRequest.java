package net.croz.nrich.registry.data.request;

import com.sun.istack.internal.NotNull;
import lombok.Data;
import net.croz.nrich.registry.data.model.SearchParameter;
import net.croz.nrich.search.api.model.SortProperty;

import java.util.List;

@Data
public class RegistryListRequest  {

    @NotNull
    private String classFullName;

    @NotNull
    private Integer start;

    @NotNull
    private Integer limit;

    @NotNull
    private SearchParameter searchParameter;

    private List<SortProperty> sortPropertyList;

}
