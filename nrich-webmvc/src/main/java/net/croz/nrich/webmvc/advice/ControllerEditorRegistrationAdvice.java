package net.croz.nrich.webmvc.advice;

import lombok.RequiredArgsConstructor;
import net.croz.nrich.webmvc.service.TransientPropertyResolverService;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

import java.util.List;

@RequiredArgsConstructor
@ControllerAdvice
public class ControllerEditorRegistrationAdvice {

    private final boolean convertEmptyStringsToNull;

    private final boolean ignoreTransientFields;

    private final TransientPropertyResolverService transientPropertyResolverService;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        if (convertEmptyStringsToNull) {
            binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
        }

        if (ignoreTransientFields) {
            Object target = binder.getTarget();

            if (target == null) {
                return;
            }

            List<String> transientPropertyList = transientPropertyResolverService.resolveTransientPropertyList(target.getClass());

            binder.setDisallowedFields(transientPropertyList.toArray(new String[0]));
        }
    }
}
