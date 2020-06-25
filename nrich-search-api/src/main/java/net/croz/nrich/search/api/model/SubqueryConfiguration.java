package net.croz.nrich.search.api.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import net.croz.nrich.search.api.model.property.SearchPropertyJoin;

@Setter
@Getter
@Builder
public class SubqueryConfiguration {

    private Class<?> rootEntity;

    private SearchPropertyJoin joinBy;

    private String propertyPrefix;

    private String restrictionPropertyHolder;

}
