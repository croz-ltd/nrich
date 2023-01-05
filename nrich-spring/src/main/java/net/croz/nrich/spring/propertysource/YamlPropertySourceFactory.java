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

package net.croz.nrich.spring.propertysource;

import lombok.SneakyThrows;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Properties;

public class YamlPropertySourceFactory implements PropertySourceFactory {

    private static final String RESOURCE_NAME_FORMAT = "%s@%s";

    @Override
    public PropertySource<?> createPropertySource(String name, EncodedResource encodedResource) {
        YamlPropertiesFactoryBean yamlPropertiesFactoryBean = new YamlPropertiesFactoryBean();
        Resource resource = encodedResource.getResource();

        yamlPropertiesFactoryBean.setResources(resource);

        Properties properties = loadProperties(yamlPropertiesFactoryBean);
        String resourceName = getNameForResource(name, resource);

        Assert.notNull(properties, "Specified properties are null!");

        return new PropertiesPropertySource(resourceName, properties);
    }

    @SneakyThrows
    private Properties loadProperties(YamlPropertiesFactoryBean yamlPropertiesFactoryBean) {
        try {
            return yamlPropertiesFactoryBean.getObject();
        }
        catch (IllegalStateException exception) {
            // YamlProcessor wraps exceptions in IllegalStateException while processPropertySource method in ConfigurationClassParser expects original exception when using ignoreResourceNotFound
            throw exception.getCause();
        }
    }

    private String getNameForResource(String name, Resource resource) {
        String resourceName = name;

        if (!StringUtils.hasText(resourceName)) {
            resourceName = resource.getDescription();
        }

        if (!StringUtils.hasText(resourceName)) {
            resourceName = String.format(RESOURCE_NAME_FORMAT, resource.getClass().getSimpleName(), System.identityHashCode(resource));
        }

        return resourceName;
    }
}
