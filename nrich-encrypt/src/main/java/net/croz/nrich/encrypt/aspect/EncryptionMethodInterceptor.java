package net.croz.nrich.encrypt.aspect;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.croz.nrich.encrypt.constants.EncryptConstants;
import net.croz.nrich.encrypt.model.EncryptionConfiguration;
import net.croz.nrich.encrypt.model.EncryptionContext;
import net.croz.nrich.encrypt.model.EncryptionOperation;
import net.croz.nrich.encrypt.service.DataEncryptionService;
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
public class EncryptionMethodInterceptor extends BaseDataEncryptionAdvice implements MethodInterceptor {

    private final DataEncryptionService dataEncryptionService;

    private final List<EncryptionConfiguration> encryptionConfigurationList;

    private final List<String> ignoredMethodList;

    @Override
    protected DataEncryptionService getDataEncryptionService() {
        return dataEncryptionService;
    }

    @Override
    public Object invoke(final MethodInvocation invocation) throws Throwable {
        if (!(invocation instanceof ProxyMethodInvocation)) {
            log.debug("Found method: {} was not instance of ProxyMethodInvocation it will not be advised!", methodName(invocation));

            return invocation.proceed();
        }

        final ProxyMethodInvocation proxyMethodInvocation = (ProxyMethodInvocation) invocation;
        final String methodName = methodName(invocation);
        final String anyMethodName = anyClassMethod(invocation);

        if (Optional.ofNullable(ignoredMethodList).orElse(Collections.emptyList()).contains(methodName)) {
            return invocation.proceed();
        }

        final List<EncryptionConfiguration> foundConfigurationList = encryptionConfigurationList.stream()
                .filter(configuration -> methodName.equals(configuration.getMethodToEncryptDecrypt()) || anyMethodName.equals(configuration.getMethodToEncryptDecrypt()))
                .collect(Collectors.toList());

        if (!foundConfigurationList.isEmpty()) {
            final EncryptionConfiguration decryptArgumentsConfiguration = findEncryptionConfigurationForOperation(foundConfigurationList, EncryptionOperation.DECRYPT);
            final EncryptionConfiguration encryptResultConfiguration = findEncryptionConfigurationForOperation(foundConfigurationList, EncryptionOperation.ENCRYPT);
            final Object[] arguments = invocation.getArguments();

            final List<Object> argumentList = Arrays.asList(arguments);
            Object[] decryptedArguments = null;

            if (decryptArgumentsConfiguration != null) {

                log.debug("Found decrypt arguments configuration: {} for method: {}", decryptArgumentsConfiguration, methodName);

                final EncryptionContext context = EncryptionContext.builder().fullyQualifiedMethodName(methodName).methodArguments(argumentList).methodDecryptedArguments(argumentList).currentUsername(currentUsername()).build();

                decryptedArguments = decryptArguments(context, arguments, decryptArgumentsConfiguration.getPropertyToEncryptDecryptList());

                proxyMethodInvocation.setArguments(decryptedArguments);
            }

            if (encryptResultConfiguration != null) {
                log.debug("Found encrypt result configuration: {} for method: {}", encryptResultConfiguration, methodName);

                final List<Object> decryptedArgumentList = Arrays.asList(decryptedArguments == null ? arguments : decryptedArguments);

                final EncryptionContext context = EncryptionContext.builder().fullyQualifiedMethodName(methodName).methodArguments(argumentList).methodDecryptedArguments(decryptedArgumentList).currentUsername(currentUsername()).build();

                return encryptResult(context, proxyMethodInvocation.proceed(), encryptResultConfiguration.getPropertyToEncryptDecryptList());
            }
        }

        return proxyMethodInvocation.proceed();
    }

    private String methodName(final MethodInvocation invocation) {
        return String.format(EncryptConstants.METHOD_NAME_FORMAT, invocation.getThis().getClass().getName(), invocation.getMethod().getName());
    }

    private String anyClassMethod(final MethodInvocation invocation) {
        return String.format(EncryptConstants.METHOD_NAME_FORMAT, invocation.getThis().getClass().getName(), EncryptConstants.ANY_METHOD_PATTERN);
    }

    private EncryptionConfiguration findEncryptionConfigurationForOperation(final List<EncryptionConfiguration> encryptionConfigurationList, final EncryptionOperation encryptionOperation) {
        return encryptionConfigurationList.stream()
                .filter(configuration -> configuration.getEncryptionOperation() == encryptionOperation)
                .findFirst()
                .orElse(null);
    }
}
