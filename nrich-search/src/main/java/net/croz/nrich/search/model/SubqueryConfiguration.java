package net.croz.nrich.search.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class SubqueryConfiguration {

    private Class<?> rootEntity;

    private SearchPropertyJoin joinBy;

    private String propertyPrefix;

    private String restrictionPropertyHolder;

}
