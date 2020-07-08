package net.croz.nrich.registry.core.constants;

public final class RegistryEnversConstants {

    public static final String ENVERS_AUDITED_ANNOTATION = "org.hibernate.envers.Audited";

    public static final String ENVERS_REVISION_ENTITY_ANNOTATION = "org.hibernate.envers.RevisionEntity";

    public static final String ENVERS_REVISION_TIMESTAMP_ANNOTATION = "org.hibernate.envers.RevisionTimestamp";

    public static final String ENVERS_REVISION_NUMBER_ANNOTATION = "org.hibernate.envers.RevisionNumber";

    public static final String REVISION_TIMESTAMP_PROPERTY_NAME = "revisionTimestamp";

    public static final String REVISION_NUMBER_PROPERTY_NAME = "revisionNumber";

    public static final String REVISION_TIMESTAMP_PROPERTY_DEFAULT_ORIGINAL_NAME = "timestamp";

    public static final String REVISION_NUMBER_PROPERTY_DEFAULT_ORIGINAL_NAME = "id";

    public static final String REVISION_TYPE_PROPERTY_NAME = "revisionType";

    private RegistryEnversConstants() {
    }
}
