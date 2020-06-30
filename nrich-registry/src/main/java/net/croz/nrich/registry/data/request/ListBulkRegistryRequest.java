package net.croz.nrich.registry.data.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Setter
@Getter
public class ListBulkRegistryRequest {

    @Size(min = 1)
    @Valid
    @NotNull
    private List<ListRegistryRequest> registryRequestList;

}
