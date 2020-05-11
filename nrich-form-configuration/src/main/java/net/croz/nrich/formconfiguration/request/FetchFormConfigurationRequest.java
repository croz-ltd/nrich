package net.croz.nrich.formconfiguration.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Setter
@Getter
public class FetchFormConfigurationRequest {

    @NotNull
    @Min(value = 1)
    private List<String> formIdList;

}
