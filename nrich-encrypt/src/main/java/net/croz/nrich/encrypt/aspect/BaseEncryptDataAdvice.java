/*
 *  Copyright 2020-2022 CROZ d.o.o, the original author or authors.
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
import net.croz.nrich.encrypt.api.model.EncryptionContext;
import net.croz.nrich.encrypt.api.service.DataEncryptionService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public abstract class BaseEncryptDataAdvice {

    private static final String REACTOR_PACKAGE_NAME = "reactor.core.publisher";

    public <T> T encryptResult(EncryptionContext encryptionContext, T result, List<String> pathToEncryptList) {
        T encryptedResult;
        if (isCompletableFutureResult(result)) {
            encryptedResult = encryptCompletableFuture(encryptionContext, pathToEncryptList, result);
        }
        else if (Boolean.TRUE.equals(isReactorResult(result))) {
            encryptedResult = new ReactorEncryptor(getDataEncryptionService()).encryptReactorResult(encryptionContext, pathToEncryptList, result);
        }
        else {
            encryptedResult = getDataEncryptionService().encryptData(result, pathToEncryptList, encryptionContext);
        }

        return encryptedResult;
    }

    public Object[] decryptArguments(EncryptionContext encryptionContext, Object[] argumentList, List<String> pathToDecryptList) {
        return Arrays.stream(argumentList)
            .map(argument -> decryptArgument(encryptionContext, argument, pathToDecryptList))
            .toArray();
    }

    public Object decryptArgument(EncryptionContext encryptionContext, Object argument, List<String> pathToDecryptList) {
        return getDataEncryptionService().decryptData(argument, pathToDecryptList, encryptionContext);
    }

    protected abstract DataEncryptionService getDataEncryptionService();

    private boolean isCompletableFutureResult(Object forEncryption) {
        return forEncryption instanceof CompletableFuture;
    }

    private Boolean isReactorResult(Object forEncryption) {
        return forEncryption != null && forEncryption.getClass().getPackage().getName().equals(REACTOR_PACKAGE_NAME);
    }

    private <T> T encryptCompletableFuture(EncryptionContext encryptionContext, List<String> pathToEncryptList, T forEncryption) {
        @SuppressWarnings("unchecked")
        T encryptedFuture = (T) ((CompletableFuture<?>) forEncryption).thenApply(completedResult -> getDataEncryptionService().encryptData(completedResult, pathToEncryptList, encryptionContext));

        return encryptedFuture;
    }

    @RequiredArgsConstructor
    private static class ReactorEncryptor {

        private final DataEncryptionService dataEncryptionService;

        public <T> T encryptReactorResult(EncryptionContext encryptionContext, List<String> pathToEncryptList, T forEncryption) {
            if (forEncryption instanceof Mono) {
                @SuppressWarnings("unchecked")
                T encryptedMono = (T) ((Mono<?>) forEncryption).map(completedResult -> dataEncryptionService.encryptData(completedResult, pathToEncryptList, encryptionContext));

                return encryptedMono;
            }
            else if (forEncryption instanceof Flux) {
                @SuppressWarnings("unchecked")
                T encryptedFlux = (T) ((Flux<?>) forEncryption).map(completedResult -> dataEncryptionService.encryptData(completedResult, pathToEncryptList, encryptionContext));

                return encryptedFlux;
            }

            return forEncryption;
        }
    }
}
