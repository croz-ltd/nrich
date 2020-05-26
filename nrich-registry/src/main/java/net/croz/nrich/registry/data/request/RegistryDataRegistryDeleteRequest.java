package net.croz.nrich.registry.data.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class RegistryDataRegistryDeleteRequest {

    @NotNull
    private String classFullName;

    @NotNull
    private Long id;

    @NotNull
    private Long version;

}
