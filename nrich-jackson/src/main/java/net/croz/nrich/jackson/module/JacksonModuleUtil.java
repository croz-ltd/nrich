package net.croz.nrich.jackson.module;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import net.croz.nrich.jackson.deserializer.ConvertEmptyStringToNullDeserializer;
import net.croz.nrich.jackson.serializer.EntityClassSerializerModifier;

import java.util.List;

public final class JacksonModuleUtil {

    public static final String CONVERT_EMPTY_STRING_TO_NULL_MODULE_NAME = "convertEmptyStringToNullModule";

    public static final String CLASS_NAME_SERIALIZER_MODULE = "classNameSerializerModule";

    private JacksonModuleUtil() {
    }

    public static Module convertEmptyStringToNullModule() {
        final SimpleModule simpleModule = new SimpleModule(JacksonModuleUtil.CONVERT_EMPTY_STRING_TO_NULL_MODULE_NAME);

        simpleModule.addDeserializer(String.class, new ConvertEmptyStringToNullDeserializer());

        return simpleModule;
    }

    public static Module classNameSerializerModule(final boolean serializeEntityAnnotatedClasses, final List<String> packageList) {
        final SimpleModule simpleModule = new SimpleModule(JacksonModuleUtil.CLASS_NAME_SERIALIZER_MODULE);

        simpleModule.setSerializerModifier(new EntityClassSerializerModifier(serializeEntityAnnotatedClasses, packageList));

        return simpleModule;
    }
}
