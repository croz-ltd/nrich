package net.croz.nrich.registry.data.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Setter
@Getter
public class UpdateRegistryServiceRequest {

    @NotNull
    private String classFullName;

    @NotNull
    private Object id;

    @Valid
    @NotNull
    private Object entityData;

}
