package net.croz.nrich.encrypt.aspect.stub;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.Future;

@RequiredArgsConstructor
@Getter
public class EncryptDataAspectTestServiceResult {

    private final String value;

}
