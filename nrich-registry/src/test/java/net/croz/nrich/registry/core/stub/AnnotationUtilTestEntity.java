package net.croz.nrich.registry.core.stub;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Valid
public class AnnotationUtilTestEntity {

    @NotNull
    public String name;

}
