package net.croz.nrich.registry.data.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Setter
@Getter
public class CreateRegistryRequest {

    @NotNull
    private String classFullName;

    @NotNull
    private String entityData;

}
