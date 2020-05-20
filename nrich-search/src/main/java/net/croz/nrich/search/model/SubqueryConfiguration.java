package net.croz.nrich.search.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class SubqueryConfiguration {

    private Class<?> rootEntity;

    private SearchPropertyJoin joinBy;

    private String propertyPrefix;

    private String restrictionPropertyHolder;

}
