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
