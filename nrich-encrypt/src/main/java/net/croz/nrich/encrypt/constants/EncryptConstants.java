package net.croz.nrich.encrypt.constants;

public final class EncryptConstants {

    public static final String METHOD_NAME_FORMAT = "%s.%s";

    public static final String ANY_METHOD_PATTERN = "*";

    public static final String EXECUTION_METHOD_POINTCUT = "execution(* %s(..))";

    public static final String EXECUTION_METHOD_OR_SEPARATOR = " || ";

    private EncryptConstants() {
    }
}
