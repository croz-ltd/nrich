package net.croz.nrich.formconfiguration.api.request;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Setter
@Getter
@EqualsAndHashCode
public class FetchFormConfigurationRequest {

    @NotNull
    @Size(min = 1)
    private List<String> formIdList;

}
