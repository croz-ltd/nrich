package net.croz.nrich.registry.data.request;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
public class UpdateRegistryServiceRequest {

    @NotNull
    private String classFullName;

    @NotNull
    private Long id;

    @Valid
    @NotNull
    private Object entityData;

}
