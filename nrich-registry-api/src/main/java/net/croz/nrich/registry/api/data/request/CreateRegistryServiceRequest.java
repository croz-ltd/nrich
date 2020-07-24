package net.croz.nrich.registry.api.data.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Request holding data for registry entity creation.
 */
@Setter
@Getter
public class CreateRegistryServiceRequest {

    /**
     * Class name of registry entity.
     */
    @NotNull
    private String classFullName;

    /**
     * Entity data for creation.
     */
    @Valid
    @NotNull
    private Object entityData;

}
