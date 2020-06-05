package net.croz.nrich.registry.data.stub;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Setter
@Getter
public class RegistryTestEntityUpdateRequest {

    @NotNull
    private String name;

    @NotNull
    private Integer age;

}
