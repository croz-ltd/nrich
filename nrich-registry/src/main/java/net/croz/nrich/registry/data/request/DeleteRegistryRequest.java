package net.croz.nrich.registry.data.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * Request holding data for registry entity deletion.
 */
@Setter
@Getter
public class DeleteRegistryRequest {

    /**
     * Class name of registry entity.
     */
    @NotNull
    private String classFullName;

    /**
     * Registry entity id.
     */
    @NotNull
    private Object id;

}
