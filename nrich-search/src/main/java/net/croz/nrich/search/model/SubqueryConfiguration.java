package net.croz.nrich.search.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class SubqueryConfiguration {

    private Class<?> rootEntity;

    private SearchPropertyJoin joinBy;

    private String propertyPrefix;

    private String restrictionPropertyHolder;

}
