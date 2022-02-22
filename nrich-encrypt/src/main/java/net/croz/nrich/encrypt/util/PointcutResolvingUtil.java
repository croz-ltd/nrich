package net.croz.nrich.encrypt.util;

import net.croz.nrich.encrypt.api.model.EncryptionConfiguration;

import java.util.List;
import java.util.stream.Collectors;

public final class PointcutResolvingUtil {

    public static final String EXECUTION_METHOD_POINTCUT = "execution(* %s(..))";

    public static final String EXECUTION_METHOD_OR_SEPARATOR = " || ";

    private PointcutResolvingUtil() {
    }

    public static String resolvePointcutFromEncryptionConfigurationList(List<EncryptionConfiguration> encryptionConfigurationList) {
        return encryptionConfigurationList.stream()
                .map(EncryptionConfiguration::getMethodToEncryptDecrypt)
                .map(method -> String.format(EXECUTION_METHOD_POINTCUT, method))
                .collect(Collectors.joining(EXECUTION_METHOD_OR_SEPARATOR));
    }
}
