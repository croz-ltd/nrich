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
import net.croz.nrich.encrypt.api.annotation.DecryptArgument;
import net.croz.nrich.encrypt.api.annotation.EncryptResult;
import net.croz.nrich.encrypt.api.model.EncryptionContext;
import net.croz.nrich.encrypt.api.service.DataEncryptionService;
import net.croz.nrich.encrypt.constants.EncryptConstants;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@Aspect
public class EncryptDataAspect extends BaseEncryptDataAdvice {

    private final DataEncryptionService dataEncryptionService;

    @Around("execution(* *(.., @net.croz.nrich.encrypt.api.annotation.DecryptArgument (*), ..)))")
    public Object aroundDecryptAnnotatedMethods(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Signature signature = proceedingJoinPoint.getSignature();
        Object[] arguments = proceedingJoinPoint.getArgs();

        if (signature instanceof MethodSignature && arguments.length > 0) {
            MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
            String methodName = methodSignature.getMethod().getName();
            Class<?>[] parameterTypes = methodSignature.getMethod().getParameterTypes();
            Annotation[][] parameterAnnotationList = proceedingJoinPoint.getTarget().getClass().getMethod(methodName, parameterTypes).getParameterAnnotations();

            EncryptionContext context = createEncryptionContext(methodSignature, arguments);

            Object[] decryptedArguments = IntStream.range(0, arguments.length).mapToObj(index -> {
                DecryptArgument argumentAnnotation = decryptArgumentAnnotation(parameterAnnotationList[index]);

                if (argumentAnnotation == null) {
                    return arguments[index];
                }

                return decryptArgument(context, arguments[index], Arrays.asList(argumentAnnotation.argumentPathList()));

            }).toArray();

            return proceedingJoinPoint.proceed(decryptedArguments);
        }

        return proceedingJoinPoint.proceed(arguments);
    }

    @Around("@annotation(annotation)")
    public Object aroundEncryptAnnotatedMethods(ProceedingJoinPoint proceedingJoinPoint, EncryptResult annotation) throws Throwable {
        Signature signature = proceedingJoinPoint.getSignature();
        Object[] arguments = proceedingJoinPoint.getArgs();
        EncryptionContext context = createEncryptionContext(signature, arguments);

        Object result = proceedingJoinPoint.proceed(arguments);

        result = encryptResult(context, result, Arrays.asList(annotation.resultPathList()));

        return result;
    }

    @Override
    protected DataEncryptionService getDataEncryptionService() {
        return dataEncryptionService;
    }

    private DecryptArgument decryptArgumentAnnotation(Annotation[] annotationList) {
        return (DecryptArgument) Arrays.stream(annotationList)
            .filter(DecryptArgument.class::isInstance)
            .findFirst()
            .orElse(null);
    }

    private EncryptionContext createEncryptionContext(Signature signature, Object[] arguments) {
        List<Object> argumentList = Arrays.asList(arguments);
        String methodName = String.format(EncryptConstants.METHOD_NAME_FORMAT, signature.getDeclaringType().getName(), signature.getName());

        return EncryptionContext.builder()
            .fullyQualifiedMethodName(methodName)
            .methodArguments(argumentList)
            .methodDecryptedArguments(argumentList)
            .build();
    }
}
