package net.croz.nrich.formconfiguration.api.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * Holder for a list of form ids for which to fetch form configuration.
 */
@Setter
@Getter
public class FetchFormConfigurationRequest {

    @NotNull
    @Size(min = 1)
    private List<String> formIdList;

}
