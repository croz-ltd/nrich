package net.croz.nrich.jackson.module;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import net.croz.nrich.jackson.deserializer.ConvertEmptyStringToNullDeserializer;

public final class JacksonModuleUtil {

    public static final String CONVERT_EMPTY_STRING_TO_NULL_MODULE_NAME = "convertEmptyStringToNullModule";

    private JacksonModuleUtil() {
    }

    public static Module convertEmptyStringToNullModule() {
        final SimpleModule simpleModule = new SimpleModule(JacksonModuleUtil.CONVERT_EMPTY_STRING_TO_NULL_MODULE_NAME);

        simpleModule.addDeserializer(String.class, new ConvertEmptyStringToNullDeserializer());

        return simpleModule;
    }
}
