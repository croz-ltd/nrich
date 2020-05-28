package net.croz.nrich.registry.data.request;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class BulkListRegistryRequest {

    @Size(min = 1)
    @Valid
    @NotNull
    private List<ListRegistryRequest> registryRequestList;

}
