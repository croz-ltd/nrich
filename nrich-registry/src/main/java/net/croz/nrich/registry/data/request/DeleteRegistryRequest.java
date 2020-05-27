package net.croz.nrich.registry.data.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class DeleteRegistryRequest {

    @NotNull
    private String classFullName;

    @NotNull
    private Long id;

}
