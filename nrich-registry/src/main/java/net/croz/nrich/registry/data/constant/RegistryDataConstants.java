package net.croz.nrich.registry.data.constant;

public final class RegistryDataConstants {

    public static final String PROPERTY_PREFIX_FORMAT = "%s.%s";

    public static final String QUERY_PARAMETER_FORMAT = "%s = :%s";

    public static final String FIND_QUERY = "from %s where %s";

    public static final String FIND_QUERY_SEPARATOR = " and ";

    public static final String CREATE_REQUEST_SUFFIX = "%sCreateRequest";

    public static final String UPDATE_REQUEST_SUFFIX = "%sUpdateRequest";

    public static final String REQUEST_SUFFIX = "%sRequest";

    public static final String REGISTRY_FORM_ID_FORMAT = "%s:::%s";

    public static final String REGISTRY_FORM_ID_CREATE_SUFFIX = "create";

    public static final String REGISTRY_FORM_ID_UPDATE_SUFFIX = "update";

    private RegistryDataConstants() {
    }

}
