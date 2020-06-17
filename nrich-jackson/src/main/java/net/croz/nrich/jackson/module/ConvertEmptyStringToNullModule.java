package net.croz.nrich.jackson.module;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import net.croz.nrich.jackson.deserializer.ConvertEmptyStringToNullDeserializer;

public final class ConvertEmptyStringToNullModule {

    private ConvertEmptyStringToNullModule() {
    }

    public static Module convertEmptyStringToNullModule() {
        final SimpleModule simpleModule = new SimpleModule(ConvertEmptyStringToNullModule.class.getSimpleName());

        simpleModule.addDeserializer(String.class, new ConvertEmptyStringToNullDeserializer());

        return simpleModule;
    }
}
