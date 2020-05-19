package net.croz.nrich.search.properties;

import lombok.Data;

import java.time.temporal.Temporal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

// TODO move to search configuration and have a default value
@Data
public class SearchProperties {

    private List<String> searchIgnoredFieldList = Collections.singletonList("searchConfiguration");

    private List<Class<?>> rangeQuerySupportedClassList = Arrays.asList(java.sql.Date.class, java.util.Date.class, Temporal.class, Number.class);

    private String defaultSubqueryJoinAttribute = "id";

    private String rangeQueryFromIncludingSuffix = "FromIncluding";

    private String rangeQueryFromSuffix = "From";

    private String rangeQueryToIncludingSuffix = "ToIncluding";

    private String rangeQueryToSuffix = "To";

}
