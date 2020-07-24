package net.croz.nrich.registry.api.data.request;

import lombok.Getter;
import lombok.Setter;
import net.croz.nrich.search.api.model.sort.SortProperty;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Request holding data for fetching a list of registry entities.
 */
@Setter
@Getter
public class ListRegistryRequest {

    /**
     * Class name of registry entity.
     */
    @NotNull
    private String classFullName;

    /**
     * Page number.
     */
    @Min(0)
    @NotNull
    private Integer pageNumber;

    /**
     * Number of entities to fetch,.
     */
    @Max(1000)
    @NotNull
    private Integer pageSize;

    /**
     * Search parameters.
     */
    private SearchParameter searchParameter;

    /**
     * List of {@link SortProperty} instances.
     */
    private List<SortProperty> sortPropertyList;

}
