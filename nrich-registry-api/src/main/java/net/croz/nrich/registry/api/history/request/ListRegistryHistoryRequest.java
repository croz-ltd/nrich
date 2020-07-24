package net.croz.nrich.registry.api.history.request;

import lombok.Getter;
import lombok.Setter;
import net.croz.nrich.search.api.model.sort.SortProperty;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Request holding data for fetching history list of registry entities.
 */
@Setter
@Getter
public class ListRegistryHistoryRequest {

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
    @Max(100)
    @NotNull
    private Integer pageSize;

    /**
     * Optional id of registry if not set history for all registry entities of specific type will be fetched.
     */
    private Object registryRecordId;

    /**
     * List of {@link SortProperty} instances.
     */
    private List<SortProperty> sortPropertyList;

}
