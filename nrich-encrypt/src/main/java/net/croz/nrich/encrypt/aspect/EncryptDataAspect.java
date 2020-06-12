package net.croz.nrich.encrypt.aspect;

import lombok.RequiredArgsConstructor;
import net.croz.nrich.encrypt.annotation.DecryptArgument;
import net.croz.nrich.encrypt.annotation.EncryptResult;
import net.croz.nrich.encrypt.constants.EncryptConstants;
import net.croz.nrich.encrypt.model.EncryptionContext;
import net.croz.nrich.encrypt.service.DataEncryptionService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Aspect
public class EncryptDataAspect extends BaseDataEncryptionAdvice {

    private final DataEncryptionService dataEncryptionService;

    /**
     * Pointcut that matches public methods with {@link DecryptArgument} annotation.
     */
    @Pointcut("execution(@net.croz.nrich.encrypt.annotation.DecryptArgument * *(..))")
    public void decryptAnnotatedMethods() {
    }

    /**
     * Pointcut that matches public methods with {@link EncryptResult} annotation.
     */
    @Pointcut("execution(@net.croz.nrich.encrypt.annotation.EncryptResult * *(..))")
    public void encryptAnnotatedMethods() {
    }

    @Around("decryptAnnotatedMethods()")
    public Object aroundDecryptAnnotatedMethods(final ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        final Signature signature = proceedingJoinPoint.getSignature();
        final DecryptArgument annotation = methodAnnotation(signature, DecryptArgument.class);

        final Object[] arguments = proceedingJoinPoint.getArgs();
        final List<Object> argumentList = Arrays.asList(arguments);

        if (annotation != null && annotation.argumentPathList().length > 0) {
            final EncryptionContext context = EncryptionContext.builder().fullyQualifiedMethodName(methodName(signature)).methodArguments(argumentList).methodDecryptedArguments(argumentList).currentUsername(currentUsername()).build();
            final Object[] decryptedArguments = decryptArguments(context, arguments, Arrays.asList(annotation.argumentPathList()));

            return proceedingJoinPoint.proceed(decryptedArguments);
        }

        return proceedingJoinPoint.proceed(arguments);
    }

    @Around("encryptAnnotatedMethods()")
    public Object aroundEncryptAnnotatedMethods(final ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        final Signature signature = proceedingJoinPoint.getSignature();
        final EncryptResult annotation = methodAnnotation(signature, EncryptResult.class);

        final Object[] arguments = proceedingJoinPoint.getArgs();
        final List<Object> argumentList = Arrays.asList(arguments);
        Object result = proceedingJoinPoint.proceed(arguments);

        if (annotation != null && annotation.resultPathList().length > 0) {
            final EncryptionContext context = EncryptionContext.builder().fullyQualifiedMethodName(methodName(signature)).methodArguments(argumentList).methodDecryptedArguments(argumentList).currentUsername(currentUsername()).build();

            result = encryptResult(context, result, Arrays.asList(annotation.resultPathList()));
        }

        return result;
    }

    @Override
    protected DataEncryptionService getDataEncryptionService() {
        return dataEncryptionService;
    }

    private <T extends Annotation> T methodAnnotation(final Signature signature, final Class<T> annotationType) {
        T annotation = null;
        if (signature instanceof MethodSignature) {
            annotation = ((MethodSignature) signature).getMethod().getAnnotation(annotationType);
        }

        return annotation;
    }

    private String methodName(final Signature signature) {
        return String.format(EncryptConstants.METHOD_NAME_FORMAT, signature.getDeclaringType().getName(), signature.getName());
    }
}
