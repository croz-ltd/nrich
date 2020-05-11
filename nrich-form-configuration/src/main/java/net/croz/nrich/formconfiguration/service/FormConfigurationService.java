package net.croz.nrich.formconfiguration.service;

import net.croz.nrich.formconfiguration.model.FormConfiguration;
import net.croz.nrich.formconfiguration.request.FetchFormConfigurationRequest;

import java.util.List;

public interface FormConfigurationService {

    List<FormConfiguration> fetchFormConfigurationList(FetchFormConfigurationRequest request);

}
