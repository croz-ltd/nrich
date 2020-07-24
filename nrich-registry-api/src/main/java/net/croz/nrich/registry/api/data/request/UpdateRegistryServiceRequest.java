package net.croz.nrich.registry.api.data.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Request holding data for registry entity update.
 */
@Setter
@Getter
public class UpdateRegistryServiceRequest {

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

    /**
     * Entity data for update.
     */
    @Valid
    @NotNull
    private Object entityData;

}
