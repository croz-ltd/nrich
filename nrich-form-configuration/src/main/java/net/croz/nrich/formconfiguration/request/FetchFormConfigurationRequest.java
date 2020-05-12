package net.croz.nrich.formconfiguration.request;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class FetchFormConfigurationRequest {

    @NotNull
    @Min(value = 1)
    private List<String> formIdList;

}
