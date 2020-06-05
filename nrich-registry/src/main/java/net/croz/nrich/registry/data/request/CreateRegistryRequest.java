package net.croz.nrich.registry.data.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CreateRegistryRequest {

    @NotNull
    private String classFullName;

    @NotNull
    private String entityData;

}
