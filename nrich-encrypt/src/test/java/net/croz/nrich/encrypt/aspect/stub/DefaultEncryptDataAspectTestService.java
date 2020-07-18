package net.croz.nrich.encrypt.aspect.stub;

import net.croz.nrich.encrypt.api.annotation.DecryptArgument;
import net.croz.nrich.encrypt.api.annotation.EncryptResult;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class DefaultEncryptDataAspectTestService implements EncryptDataAspectTestService {

    @EncryptResult(resultPathList = "value")
    @Override
    public EncryptDataAspectTestServiceResult dataToEncrypt(final String value) {
        return new EncryptDataAspectTestServiceResult(value);
    }

    @EncryptResult(resultPathList = {})
    @Override
    public EncryptDataAspectTestServiceResult dataToEncryptWithInvalidAnnotation(final String value) {
        return new EncryptDataAspectTestServiceResult(value);
    }

    @EncryptResult(resultPathList = "value")
    @Override
    public Future<EncryptDataAspectTestServiceResult> dataToEncryptWithCompletableFuture(final String value) {
        return CompletableFuture.completedFuture(new EncryptDataAspectTestServiceResult(value));
    }

    @EncryptResult(resultPathList = "value")
    @Override
    public Mono<EncryptDataAspectTestServiceResult> dataToEncryptWithMono(final String value) {
        return Mono.just(new EncryptDataAspectTestServiceResult(value));
    }

    @EncryptResult(resultPathList = "value")
    @Override
    public Flux<EncryptDataAspectTestServiceResult> dataToEncryptWithFlux(final String value) {
        return Flux.just(new EncryptDataAspectTestServiceResult(value));
    }

    @Override
    public EncryptDataAspectTestServiceResult dataToDecrypt(@DecryptArgument(argumentPathList = "value") final EncryptDataAspectTestServiceResult data) {
        return data;
    }

    @Override
    public EncryptDataAspectTestServiceResult dataToDecryptWithInvalidAnnotation(@DecryptArgument(argumentPathList = {}) final EncryptDataAspectTestServiceResult data) {
        return data;
    }

    @Override
    public EncryptDataAspectTestServiceResult dataToEncryptFromConfiguration(final String value) {
        return new EncryptDataAspectTestServiceResult(value);
    }

    @Override
    public EncryptDataAspectTestServiceResult dataToDecryptFromConfiguration(final EncryptDataAspectTestServiceResult data) {
        return data;
    }
}
