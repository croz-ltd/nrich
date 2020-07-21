package net.croz.nrich.registry.api.data.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Setter
@Getter
public class CreateRegistryServiceRequest {

    @NotNull
    private String classFullName;

    @Valid
    @NotNull
    private Object entityData;

}
