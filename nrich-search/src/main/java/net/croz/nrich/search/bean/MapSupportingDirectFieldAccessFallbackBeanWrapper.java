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

package net.croz.nrich.search.bean;

import org.springframework.data.util.DirectFieldAccessFallbackBeanWrapper;

import java.util.Map;

public class MapSupportingDirectFieldAccessFallbackBeanWrapper extends DirectFieldAccessFallbackBeanWrapper {

    private final Map<String, Object> entityAsMap;

    public MapSupportingDirectFieldAccessFallbackBeanWrapper(Object entity) {
        super(entity);
        entityAsMap = entity instanceof Map ? asMap(entity) : null;
    }

    @Override
    public Object getPropertyValue(String propertyName) {
        if (entityAsMap == null) {
            return super.getPropertyValue(propertyName);
        }

        return entityAsMap.get(propertyName);
    }

    @Override
    public void setPropertyValue(String propertyName, Object value) {
        if (entityAsMap == null) {
            super.setPropertyValue(propertyName, value);
        }
        else {
            entityAsMap.put(propertyName, value);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> asMap(Object entity) {
        return (Map<String, Object>) entity;
    }

    public Map<String, Object> getEntityAsMap() {
        return entityAsMap;
    }
}
