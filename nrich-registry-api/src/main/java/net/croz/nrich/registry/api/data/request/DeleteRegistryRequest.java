package net.croz.nrich.registry.api.data.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Setter
@Getter
public class DeleteRegistryRequest {

    @NotNull
    private String classFullName;

    @NotNull
    private Object id;

}