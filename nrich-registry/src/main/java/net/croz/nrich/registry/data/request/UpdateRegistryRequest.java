package net.croz.nrich.registry.data.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UpdateRegistryRequest {

    @NotNull
    private String classFullName;

    @NotNull
    private Object id;

    @NotNull
    private String entityData;

}
