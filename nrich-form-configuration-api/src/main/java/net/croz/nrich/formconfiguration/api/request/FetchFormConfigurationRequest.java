package net.croz.nrich.formconfiguration.api.request;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Setter
@Getter
@EqualsAndHashCode
public class FetchFormConfigurationRequest {

    @NotNull
    @Min(value = 1)
    private List<String> formIdList;

}
