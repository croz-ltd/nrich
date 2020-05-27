package net.croz.nrich.registry.data.constant;

public final class RegistryDataConstants {

    public static final String ID_PARAM = "id";

    public static final String DELETE_QUERY = "delete from %s where %s = :" + ID_PARAM;

    public static final String FIND_QUERY = "from %s where %s = :" + ID_PARAM;

    private RegistryDataConstants() {
    }

}
