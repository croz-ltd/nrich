/*
 *  Copyright 2020-2023 CROZ d.o.o, the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

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
            .map(EncryptionConfiguration::methodToEncryptDecrypt)
            .map(method -> String.format(EXECUTION_METHOD_POINTCUT, method))
            .collect(Collectors.joining(EXECUTION_METHOD_OR_SEPARATOR));
    }
}
