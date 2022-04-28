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

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Signal;

import java.util.concurrent.Future;

public interface EncryptDataAspectTestService {

    EncryptDataAspectTestServiceResult dataToEncrypt(String value);

    EncryptDataAspectTestServiceResult dataToEncrypt();

    EncryptDataAspectTestServiceResult dataToEncryptWithInvalidAnnotation(String value);

    Future<EncryptDataAspectTestServiceResult> dataToEncryptWithCompletableFuture(String value);

    Mono<EncryptDataAspectTestServiceResult> dataToEncryptWithMono(String value);

    Flux<EncryptDataAspectTestServiceResult> dataToEncryptWithFlux(String value);

    Signal<EncryptDataAspectTestServiceResult> dataToEncryptWithUnsupportedReactorClass(String value);

    EncryptDataAspectTestServiceResult dataToDecrypt(EncryptDataAspectTestServiceResult data);

    EncryptDataAspectTestServiceResult dataToDecryptWithInvalidAnnotation(EncryptDataAspectTestServiceResult data);

    String textToEncrypt(String value);

    String textToDecrypt(String value, String valueToIgnore);

    EncryptDataAspectTestServiceResult dataToEncryptFromConfiguration(String value);

    EncryptDataAspectTestServiceResult dataToDecryptFromConfiguration(EncryptDataAspectTestServiceResult data);

}
