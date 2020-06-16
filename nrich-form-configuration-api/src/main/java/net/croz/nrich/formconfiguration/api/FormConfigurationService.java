package net.croz.nrich.formconfiguration.api;

import net.croz.nrich.formconfiguration.api.model.FormConfiguration;
import net.croz.nrich.formconfiguration.api.request.FetchFormConfigurationRequest;

import java.util.List;

public interface FormConfigurationService {

    List<FormConfiguration> fetchFormConfigurationList(FetchFormConfigurationRequest request);

}
