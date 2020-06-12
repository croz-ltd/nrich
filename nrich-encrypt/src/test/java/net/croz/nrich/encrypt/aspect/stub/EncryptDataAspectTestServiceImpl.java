package net.croz.nrich.encrypt.aspect.stub;

import net.croz.nrich.encrypt.annotation.DecryptArgument;
import net.croz.nrich.encrypt.annotation.EncryptResult;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class EncryptDataAspectTestServiceImpl implements EncryptDataAspectTestService {

    @EncryptResult(resultPathList = "value")
    public EncryptDataAspectTestServiceResult dataToEncrypt(final String value) {
        return new EncryptDataAspectTestServiceResult(value);
    }

    @EncryptResult(resultPathList = "value")
    public Future<EncryptDataAspectTestServiceResult> dataToEncryptWithCompletableFuture(final String value) {
        return CompletableFuture.completedFuture(new EncryptDataAspectTestServiceResult(value));
    }

    @EncryptResult(resultPathList = "value")
    public Mono<EncryptDataAspectTestServiceResult> dataToEncryptWithMono(final String value) {
        return Mono.just(new EncryptDataAspectTestServiceResult(value));
    }

    @EncryptResult(resultPathList = "value")
    public Flux<EncryptDataAspectTestServiceResult> dataToEncryptWithFlux(final String value) {
        return Flux.just(new EncryptDataAspectTestServiceResult(value));
    }

    @DecryptArgument(argumentPathList = "value")
    public EncryptDataAspectTestServiceResult dataToDecrypt(final EncryptDataAspectTestServiceResult data) {
        return data;
    }

}
