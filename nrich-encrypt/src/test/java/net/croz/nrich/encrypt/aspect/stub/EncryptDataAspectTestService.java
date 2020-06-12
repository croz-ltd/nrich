package net.croz.nrich.encrypt.aspect.stub;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.Future;

public interface EncryptDataAspectTestService {

    EncryptDataAspectTestServiceResult dataToEncrypt(String value);

    Future<EncryptDataAspectTestServiceResult> dataToEncryptWithCompletableFuture(String value);

    Mono<EncryptDataAspectTestServiceResult> dataToEncryptWithMono(String value);

    Flux<EncryptDataAspectTestServiceResult> dataToEncryptWithFlux(String value);

    EncryptDataAspectTestServiceResult dataToDecrypt(EncryptDataAspectTestServiceResult data);
}
