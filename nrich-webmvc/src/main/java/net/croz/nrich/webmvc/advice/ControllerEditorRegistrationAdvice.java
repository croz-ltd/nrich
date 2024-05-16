/*
 *  Copyright 2020-2023 CROZ d.o.o, the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

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
            Class<?> targetType = null;
            if (binder.getTarget() != null) {
                targetType = binder.getTarget().getClass();
            }
            else if (binder.getTargetType() != null) {
                targetType = binder.getTargetType().resolve();
            }

            if (targetType == null) {
                return;
            }

            List<String> transientPropertyList = transientPropertyResolverService.resolveTransientPropertyList(targetType);

            binder.setDisallowedFields(transientPropertyList.toArray(new String[0]));
        }
    }
}
