package net.croz.nrich.search.bean;

import org.springframework.data.util.DirectFieldAccessFallbackBeanWrapper;

import java.util.Map;

public class MapSupportingDirectFieldAccessFallbackBeanWrapper extends DirectFieldAccessFallbackBeanWrapper {

    private final Map<String, Object> entityAsMap;

    public MapSupportingDirectFieldAccessFallbackBeanWrapper(final Object entity) {
        super(entity);
        this.entityAsMap = asMap(entity);
    }

    @Override
    public Object getPropertyValue(final String propertyName) {
        if (entityAsMap == null) {
            return super.getPropertyValue(propertyName);
        }

        return entityAsMap.get(propertyName);
    }

    @Override
    public void setPropertyValue(final String propertyName, final Object value) {
        if (entityAsMap == null) {
            super.setPropertyValue(propertyName, value);
        }
        else {
            entityAsMap.put(propertyName, value);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> asMap(final Object entity) {
        if (entity instanceof Map) {
            return (Map<String, Object>) entity;
        }

        return null;
    }

    public Map<String, Object> getEntityAsMap() {
        return entityAsMap;
    }
}
