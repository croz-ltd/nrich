package net.croz.nrich.formconfiguration.api.service;

import net.croz.nrich.formconfiguration.api.model.FormConfiguration;

import java.util.List;

/**
 * Resolves a list of {@link FormConfiguration} instances for a list of form ids. Form id is registered with a class that holds
 * constraints for specific form and constraint configuration is resolved from that class.
 */
public interface FormConfigurationService {

    /**
     * Returns a list of {@link FormConfiguration} instances for a list of form ids.
     *
     * @param formIdList list of form ids for which to fetch form configuration
     * @return a list of of form configuration instances
     */
    List<FormConfiguration> fetchFormConfigurationList(List<String> formIdList);

}
