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

package net.croz.nrich.encrypt.aspect.stub;

import net.croz.nrich.encrypt.api.annotation.DecryptArgument;
import net.croz.nrich.encrypt.api.annotation.EncryptResult;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Signal;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class DefaultEncryptDataAspectTestService implements EncryptDataAspectTestService {

    @EncryptResult(resultPathList = "value")
    @Override
    public EncryptDataAspectTestServiceResult dataToEncrypt(String value) {
        return new EncryptDataAspectTestServiceResult(value);
    }

    @EncryptResult(resultPathList = "value")
    @Override
    public EncryptDataAspectTestServiceResult dataToEncrypt() {
        return null;
    }

    @EncryptResult
    @Override
    public EncryptDataAspectTestServiceResult dataToEncryptWithInvalidAnnotation(String value) {
        return new EncryptDataAspectTestServiceResult(value);
    }

    @EncryptResult(resultPathList = "value")
    @Override
    public Future<EncryptDataAspectTestServiceResult> dataToEncryptWithCompletableFuture(String value) {
        return CompletableFuture.completedFuture(new EncryptDataAspectTestServiceResult(value));
    }

    @EncryptResult(resultPathList = "value")
    @Override
    public Mono<EncryptDataAspectTestServiceResult> dataToEncryptWithMono(String value) {
        return Mono.just(new EncryptDataAspectTestServiceResult(value));
    }

    @EncryptResult(resultPathList = "value")
    @Override
    public Flux<EncryptDataAspectTestServiceResult> dataToEncryptWithFlux(String value) {
        return Flux.just(new EncryptDataAspectTestServiceResult(value));
    }

    @EncryptResult(resultPathList = "value")
    @Override
    public Signal<EncryptDataAspectTestServiceResult> dataToEncryptWithUnsupportedReactorClass(String value) {
        return Signal.next(new EncryptDataAspectTestServiceResult(value));
    }

    @Override
    public EncryptDataAspectTestServiceResult dataToDecrypt(@DecryptArgument(argumentPathList = "value") EncryptDataAspectTestServiceResult data) {
        return data;
    }

    @Override
    public EncryptDataAspectTestServiceResult dataToDecryptWithInvalidAnnotation(@DecryptArgument EncryptDataAspectTestServiceResult data) {
        return data;
    }

    @EncryptResult
    @Override
    public String textToEncrypt(String value) {
        return value;
    }

    @Override
    public String textToDecrypt(@DecryptArgument String value, String valueToIgnore) {
        return value;
    }

    @Override
    public EncryptDataAspectTestServiceResult dataToEncryptFromConfiguration(String value) {
        return new EncryptDataAspectTestServiceResult(value);
    }

    @Override
    public EncryptDataAspectTestServiceResult dataToDecryptFromConfiguration(EncryptDataAspectTestServiceResult data) {
        return data;
    }
}
