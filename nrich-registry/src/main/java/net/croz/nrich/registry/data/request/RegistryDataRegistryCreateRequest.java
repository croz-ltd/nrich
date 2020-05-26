package net.croz.nrich.registry.data.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class RegistryDataRegistryCreateRequest {

    @NotNull
    private String classFullName;

}
