package net.croz.nrich.validation.constraint.stub;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.croz.nrich.validation.api.constraint.NullWhen;

import java.util.function.Predicate;

@RequiredArgsConstructor
@Getter
@NullWhen(property = "property", condition = NullWhenTestRequestWithAutowiredService.Condition.class)
public class NullWhenTestRequestWithAutowiredService {

    private final String property;

    private final String differentProperty;

    @RequiredArgsConstructor
    public static class Condition implements Predicate<NullWhenTestRequestWithAutowiredService> {

        private final NullWhenTestService nullWhenTestService;

        @Override
        public boolean test(NullWhenTestRequestWithAutowiredService notNullWhenTestRequest) {
            return nullWhenTestService.reportError();
        }
    }
}
