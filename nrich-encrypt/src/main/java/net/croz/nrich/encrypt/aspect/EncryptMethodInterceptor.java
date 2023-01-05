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

package net.croz.nrich.encrypt.aspect;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.croz.nrich.encrypt.api.model.EncryptionConfiguration;
import net.croz.nrich.encrypt.api.model.EncryptionContext;
import net.croz.nrich.encrypt.api.model.EncryptionOperation;
import net.croz.nrich.encrypt.api.service.DataEncryptionService;
import net.croz.nrich.encrypt.constants.EncryptConstants;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.ProxyMethodInvocation;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class EncryptMethodInterceptor extends BaseEncryptDataAdvice implements MethodInterceptor {

    private final DataEncryptionService dataEncryptionService;

    private final List<EncryptionConfiguration> encryptionConfigurationList;

    private final List<String> ignoredMethodList;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        if (!(invocation instanceof ProxyMethodInvocation)) {
            log.debug("Found method: {} was not instance of ProxyMethodInvocation it will not be encrypted!!", methodName(invocation));

            return invocation.proceed();
        }

        ProxyMethodInvocation proxyMethodInvocation = (ProxyMethodInvocation) invocation;
        String methodName = methodName(invocation);
        String anyMethodName = anyClassMethod(invocation);

        if (Optional.ofNullable(ignoredMethodList).orElse(Collections.emptyList()).contains(methodName)) {
            return invocation.proceed();
        }

        List<EncryptionConfiguration> foundConfigurationList = encryptionConfigurationList.stream()
            .filter(configuration -> methodName.equals(configuration.getMethodToEncryptDecrypt()) || anyMethodName.equals(configuration.getMethodToEncryptDecrypt()))
            .collect(Collectors.toList());

        if (!foundConfigurationList.isEmpty()) {
            EncryptionConfiguration decryptArgumentsConfiguration = findEncryptionConfigurationForOperation(foundConfigurationList, EncryptionOperation.DECRYPT);
            EncryptionConfiguration encryptResultConfiguration = findEncryptionConfigurationForOperation(foundConfigurationList, EncryptionOperation.ENCRYPT);
            Object[] arguments = invocation.getArguments();

            Object[] decryptedArguments = null;
            if (decryptArgumentsConfiguration != null) {
                log.debug("Found decrypt arguments configuration: {} for method: {}", decryptArgumentsConfiguration, methodName);

                EncryptionContext context = createEncryptionContext(methodName, arguments);

                decryptedArguments = decryptArguments(context, arguments, decryptArgumentsConfiguration.getPropertyToEncryptDecryptList());

                proxyMethodInvocation.setArguments(decryptedArguments);
            }

            if (encryptResultConfiguration != null) {
                log.debug("Found encrypt result configuration: {} for method: {}", encryptResultConfiguration, methodName);

                EncryptionContext context = createEncryptionContext(methodName, decryptedArguments == null ? arguments : decryptedArguments);

                return encryptResult(context, proxyMethodInvocation.proceed(), encryptResultConfiguration.getPropertyToEncryptDecryptList());
            }
        }

        return proxyMethodInvocation.proceed();
    }

    @Override
    protected DataEncryptionService getDataEncryptionService() {
        return dataEncryptionService;
    }

    private String methodName(MethodInvocation invocation) {
        return String.format(EncryptConstants.METHOD_NAME_FORMAT, invocation.getThis().getClass().getName(), invocation.getMethod().getName());
    }

    private String anyClassMethod(MethodInvocation invocation) {
        return String.format(EncryptConstants.METHOD_NAME_FORMAT, invocation.getThis().getClass().getName(), EncryptConstants.ANY_METHOD_PATTERN);
    }

    private EncryptionConfiguration findEncryptionConfigurationForOperation(List<EncryptionConfiguration> encryptionConfigurationList, EncryptionOperation encryptionOperation) {
        return encryptionConfigurationList.stream()
            .filter(configuration -> configuration.getEncryptionOperation() == encryptionOperation)
            .findFirst()
            .orElse(null);
    }

    private EncryptionContext createEncryptionContext(String methodName, Object[] arguments) {
        List<Object> argumentList = Arrays.asList(arguments);

        return EncryptionContext.builder()
            .fullyQualifiedMethodName(methodName)
            .methodArguments(argumentList)
            .methodDecryptedArguments(argumentList)
            .build();
    }
}
