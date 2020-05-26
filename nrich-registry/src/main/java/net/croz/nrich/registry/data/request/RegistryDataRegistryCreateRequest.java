package net.croz.nrich.registry.data.request;

import com.sun.istack.internal.NotNull;
import lombok.Data;

@Data
public class RegistryDataRegistryCreateRequest {

    @NotNull
    private String classFullName;

}
