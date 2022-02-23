package net.croz.nrich.security.csrf.core.constants;

public final class AesCsrfTokenConstants {

    public static final String ENCRYPTION_ALGORITHM = "AES";

    public static final String CIPHER_TRANSFORMATION = "AES/GCM/NoPadding";

    public static final int AUTHENTICATION_TAG_LENGTH = 128;

    public static final int TOKEN_LENGTH = 34;

    public static final int INITIALIZATION_VECTOR_LENGTH = 12;

    private AesCsrfTokenConstants() {
    }
}
