package net.croz.nrich.registry.data.constant;

public final class RegistryDataConstants {

    public static final String ID_ATTRIBUTE = "id";

    public static final String DELETE_QUERY = "delete from %s where " + ID_ATTRIBUTE + " = :" + ID_ATTRIBUTE;

    public static final String CREATE_REQUEST_SUFFIX = "%sCreateRequest";

    public static final String REQUEST_SUFFIX = "%sRequest";

    private RegistryDataConstants() {
    }

}
