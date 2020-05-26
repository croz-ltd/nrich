package net.croz.nrich.registry.data.request;

import com.sun.istack.internal.NotNull;
import lombok.Data;

@Data
public class RegistryDataRegistryDeleteRequest {

    @NotNull
    private String classFullName;

    @NotNull
    private Long id;

    @NotNull
    private Long version;

}
