package net.croz.nrich.registry.data.constant;

public final class RegistryDataConstants {

    public static final String ENTITY_ALIAS = "entity";

    public static final String PROPERTY_SPACE_FORMAT = " %s %s ";

    public static final String QUERY_PARAMETER_FORMAT = ENTITY_ALIAS + ".%s = :%s";

    public static final String FIND_QUERY = "select " + ENTITY_ALIAS + " from %s where %s";

    public static final String FIND_QUERY_SEPARATOR = " and ";

    public static final String FIND_QUERY_JOIN_FETCH = " left join fetch " + ENTITY_ALIAS + ".%s ";

    public static final String CREATE_REQUEST_SUFFIX = "%sCreateRequest";

    public static final String UPDATE_REQUEST_SUFFIX = "%sUpdateRequest";

    public static final String REQUEST_SUFFIX = "%sRequest";

    public static final String REGISTRY_FORM_ID_FORMAT = "%s:::%s";

    public static final String REGISTRY_FORM_ID_CREATE_SUFFIX = "create";

    public static final String REGISTRY_FORM_ID_UPDATE_SUFFIX = "update";

    private RegistryDataConstants() {
    }
}
