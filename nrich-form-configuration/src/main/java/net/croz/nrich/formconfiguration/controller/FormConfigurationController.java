package net.croz.nrich.formconfiguration.controller;

import lombok.RequiredArgsConstructor;
import net.croz.nrich.formconfiguration.model.FormConfiguration;
import net.croz.nrich.formconfiguration.request.FetchFormConfigurationRequest;
import net.croz.nrich.formconfiguration.service.FormConfigurationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

// TODO decide url format (like this or maybe form-configuration in either case all urls should follow same format?)
@RequiredArgsConstructor
@RequestMapping("formConfiguration")
@RestController
public class FormConfigurationController {

    private final FormConfigurationService formConfigurationService;

    @PostMapping("fetchFormConfiguration")
    public List<FormConfiguration> fetchFormConfiguration(@Valid @RequestBody final FetchFormConfigurationRequest request) {
        return formConfigurationService.fetchFormConfigurationList(request);
    }
}
