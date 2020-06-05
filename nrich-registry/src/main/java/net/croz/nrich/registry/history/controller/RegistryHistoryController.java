package net.croz.nrich.registry.history.controller;

import lombok.RequiredArgsConstructor;
import net.croz.nrich.registry.history.model.EntityWithRevision;
import net.croz.nrich.registry.history.request.ListRegistryHistoryRequest;
import net.croz.nrich.registry.history.service.RegistryHistoryService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;

// TODO what about form configuration, ideally maybe enable dynamic registration of formId class mapping instead of explicit dependency on form-configuration module?
@RequiredArgsConstructor
@RequestMapping("nrich-registry-history")
@ResponseBody
public class RegistryHistoryController {

    private final RegistryHistoryService registryHistoryService;

    @PostMapping("list")
    public <T> Page<EntityWithRevision<T>> historyList(@RequestBody @Valid final ListRegistryHistoryRequest request) {
        return registryHistoryService.historyList(request);
    }
}
