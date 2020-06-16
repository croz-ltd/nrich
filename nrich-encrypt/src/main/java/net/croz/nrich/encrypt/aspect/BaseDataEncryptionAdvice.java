package net.croz.nrich.encrypt.aspect;

import lombok.RequiredArgsConstructor;
import net.croz.nrich.encrypt.api.model.EncryptionContext;
import net.croz.nrich.encrypt.api.service.DataEncryptionService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public abstract class BaseDataEncryptionAdvice {

    private static final String REACTOR_PACKAGE_NAME = "reactor.core.publisher";

    public <T> T encryptResult(final EncryptionContext encryptionContext, final T result, final List<String> pathToEncryptList) {
        final T encryptedResult;
        if (isCompletableFutureResult(result)) {
            encryptedResult = encryptCompletableFuture(encryptionContext, pathToEncryptList, result);
        }
        else if (isReactorResult(result)) {
            encryptedResult = new ReactorEncryptor(getDataEncryptionService()).encryptReactorResult(encryptionContext, pathToEncryptList, result);
        }
        else {
            encryptedResult = getDataEncryptionService().encryptData(result, pathToEncryptList, encryptionContext);
        }

        return encryptedResult;
    }

    public Object[] decryptArguments(final EncryptionContext encryptionContext, final Object[] argumentList, final List<String> pathToDecryptList) {
        return Arrays.stream(argumentList)
                .map(argument -> getDataEncryptionService().decryptData(argument, pathToDecryptList, encryptionContext))
                .toArray();
    }

    protected abstract DataEncryptionService getDataEncryptionService();

    protected Authentication authentication() {
        return Optional.ofNullable(SecurityContextHolder.getContext()).map(SecurityContext::getAuthentication).orElse(null);
    }

    private boolean isCompletableFutureResult(final Object forEncryption) {
        return forEncryption instanceof CompletableFuture;
    }

    private Boolean isReactorResult(final Object forEncryption) {
        return forEncryption != null && forEncryption.getClass().getPackage().getName().equals(REACTOR_PACKAGE_NAME);
    }

    private <T> T encryptCompletableFuture(final EncryptionContext encryptionContext, final List<String> pathToEncryptList, final T forEncryption) {
        @SuppressWarnings("unchecked")
        final T encryptedFuture = (T) ((CompletableFuture<?>) forEncryption).thenApply(completedResult -> getDataEncryptionService().encryptData(completedResult, pathToEncryptList, encryptionContext));

        return encryptedFuture;
    }

    @RequiredArgsConstructor
    private static class ReactorEncryptor {

        private final DataEncryptionService dataEncryptionService;

        public <T> T encryptReactorResult(final EncryptionContext encryptionContext, final List<String> pathToEncryptList, final T forEncryption) {
            if (forEncryption instanceof Mono) {
                @SuppressWarnings("unchecked")
                final T encryptedMono = (T) ((Mono<?>) forEncryption).map(completedResult -> dataEncryptionService.encryptData(completedResult, pathToEncryptList, encryptionContext));

                return encryptedMono;
            }
            else if (forEncryption instanceof Flux) {
                @SuppressWarnings("unchecked")
                final T encryptedFlux = (T) ((Flux<?>) forEncryption).map(completedResult -> dataEncryptionService.encryptData(completedResult, pathToEncryptList, encryptionContext));

                return encryptedFlux;
            }

            return forEncryption;
        }

    }
}
