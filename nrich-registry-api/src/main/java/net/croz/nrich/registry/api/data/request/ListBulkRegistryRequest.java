package net.croz.nrich.registry.api.data.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * Request holding data for fetching a list of different registry entities.
 */
@Setter
@Getter
public class ListBulkRegistryRequest {

    /**
     * List of {@link ListRegistryRequest} instances.
     */
    @Size(min = 1)
    @Valid
    @NotNull
    private List<ListRegistryRequest> registryRequestList;

}
