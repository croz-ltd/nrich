package net.croz.nrich.registry.core.constants;

public final class RegistryQueryConstants {

    public static final String PATH_SEPARATOR_REGEX = "\\.";

    public static final String ENTITY_ALIAS = "entity";

    public static final String PROPERTY_SPACE_FORMAT = " %s %s ";

    public static final String QUERY_PARAMETER_FORMAT = ENTITY_ALIAS + ".%s = :%s";

    public static final String FIND_QUERY = "select " + ENTITY_ALIAS + " from %s where %s";

    public static final String FIND_QUERY_SEPARATOR = " and ";

    public static final String FIND_QUERY_JOIN_FETCH = " left join fetch " + ENTITY_ALIAS + ".%s ";

    public static final String QUERY_JOIN_SEPARATOR = " ";

    private RegistryQueryConstants() {
    }
}
