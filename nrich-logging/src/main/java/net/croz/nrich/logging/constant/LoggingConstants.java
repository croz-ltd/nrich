package net.croz.nrich.logging.constant;

public final class LoggingConstants {

    public static final String EXCEPTION_COMPACT_LEVEL_LOG_FORMAT = "Exception occurred: [className: %s], message: %s, additionalInfoData: %s";

    public static final String EXCEPTION_FULL_LEVEL_MESSAGE_FORMAT = "Exception occurred: [className: %s], message: %s";

    public static final String EXCEPTION_FULL_LEVEL_LOG_FORMAT = "---------------- Information about above exception %s: %s ----------------";

    public static final String AUXILIARY_DATA_FORMAT = "%s: %s";

    public static final String LOGGING_VERBOSITY_LEVEL_RESOLVING_FORMAT = "%s.loggingVerbosityLevel";

    private LoggingConstants() {
    }

}
