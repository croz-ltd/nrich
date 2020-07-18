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
    public Object aroundDecryptAnnotatedMethods(final ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        final Signature signature = proceedingJoinPoint.getSignature();
        final Object[] arguments = proceedingJoinPoint.getArgs();

        if (signature instanceof MethodSignature && arguments.length > 0) {
            final List<Object> argumentList = Arrays.asList(arguments);

            final MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();

            final String methodName = methodSignature.getMethod().getName();

            final Class<?>[] parameterTypes = methodSignature.getMethod().getParameterTypes();

            final Annotation[][] parameterAnnotationList = proceedingJoinPoint.getTarget().getClass().getMethod(methodName, parameterTypes).getParameterAnnotations();

            final EncryptionContext context = EncryptionContext.builder().fullyQualifiedMethodName(methodName(signature)).methodArguments(argumentList).methodDecryptedArguments(argumentList).principal(authentication()).build();

            final Object[] decryptedArguments = new Object[arguments.length];

            IntStream.range(0, arguments.length).forEach(index -> {
                final DecryptArgument argumentAnnotation = decryptArgumentAnnotation(parameterAnnotationList[index]);

                if (argumentAnnotation != null) {
                    decryptedArguments[index] = decryptArgument(context, arguments[index], Arrays.asList(argumentAnnotation.argumentPathList()));
                }
                else {
                    decryptedArguments[index] = arguments[index];
                }
            });

            return proceedingJoinPoint.proceed(decryptedArguments);
        }

        return proceedingJoinPoint.proceed(arguments);
    }

    @Around("@annotation(annotation)")
    public Object aroundEncryptAnnotatedMethods(final ProceedingJoinPoint proceedingJoinPoint, final EncryptResult annotation) throws Throwable {
        final Signature signature = proceedingJoinPoint.getSignature();

        final Object[] arguments = proceedingJoinPoint.getArgs();
        final List<Object> argumentList = Arrays.asList(arguments);
        Object result = proceedingJoinPoint.proceed(arguments);

        if (annotation.resultPathList().length > 0) {
            final EncryptionContext context = EncryptionContext.builder().fullyQualifiedMethodName(methodName(signature)).methodArguments(argumentList).methodDecryptedArguments(argumentList).principal(authentication()).build();

            result = encryptResult(context, result, Arrays.asList(annotation.resultPathList()));
        }

        return result;
    }

    @Override
    protected DataEncryptionService getDataEncryptionService() {
        return dataEncryptionService;
    }

    private String methodName(final Signature signature) {
        return String.format(EncryptConstants.METHOD_NAME_FORMAT, signature.getDeclaringType().getName(), signature.getName());
    }

    private DecryptArgument decryptArgumentAnnotation(final Annotation[] annotationList) {
        return (DecryptArgument) Arrays.stream(annotationList)
                .filter(annotation -> annotation instanceof DecryptArgument)
                .findFirst()
                .orElse(null);
    }
}
