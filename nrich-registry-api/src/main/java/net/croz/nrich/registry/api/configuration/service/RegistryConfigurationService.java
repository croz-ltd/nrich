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

package net.croz.nrich.registry.api.configuration.service;

import net.croz.nrich.registry.api.configuration.model.RegistryGroupConfiguration;

import java.util.List;

/**
 * Resolves a list of {@link RegistryGroupConfiguration} instances. Data that will be returned and order is configured through
 * {@link net.croz.nrich.registry.api.core.model.RegistryConfiguration}.
 */
public interface RegistryConfigurationService {

    /**
     * Returns a list of {@link RegistryGroupConfiguration} instances.
     *
     * @return a list of {@link RegistryGroupConfiguration} instances.
     */
    List<RegistryGroupConfiguration> fetchRegistryGroupConfigurationList();

}
