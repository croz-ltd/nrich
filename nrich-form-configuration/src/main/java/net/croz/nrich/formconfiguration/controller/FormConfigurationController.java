package net.croz.nrich.formconfiguration.controller;

import lombok.RequiredArgsConstructor;
import net.croz.nrich.formconfiguration.api.model.FormConfiguration;
import net.croz.nrich.formconfiguration.api.request.FetchFormConfigurationRequest;
import net.croz.nrich.formconfiguration.api.service.FormConfigurationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("nrich/form/configuration")
public class FormConfigurationController {

    private final FormConfigurationService formConfigurationService;

    @ResponseBody
    @PostMapping("fetch")
    public List<FormConfiguration> fetch(@RequestBody @Valid final FetchFormConfigurationRequest request) {
        return formConfigurationService.fetchFormConfigurationList(request.getFormIdList());
    }
}
