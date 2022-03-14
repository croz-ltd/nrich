/*
 *  Copyright 2020-2022 CROZ d.o.o, the original author or authors.
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
     * @return a list of form configuration instances
     */
    List<FormConfiguration> fetchFormConfigurationList(List<String> formIdList);

}
