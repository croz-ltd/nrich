package net.croz.nrich.formconfiguration.stub;

import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Setter
@Getter
public class FormConfigurationServiceNestedTestRequest {

    @NotNull
    private String name;

    @Valid
    private FormConfigurationServiceTestRequest request;

}
