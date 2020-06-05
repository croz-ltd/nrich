package net.croz.nrich.registry.data.request;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
public class CreateRegistryServiceRequest {

    @NotNull
    private String classFullName;

    @Valid
    @NotNull
    private Object entityData;

}
