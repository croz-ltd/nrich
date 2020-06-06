package net.croz.nrich.registry.data.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Setter
@Getter
public class UpdateRegistryRequest {

    @NotNull
    private String classFullName;

    @NotNull
    private Object id;

    @NotNull
    private String entityData;

}
