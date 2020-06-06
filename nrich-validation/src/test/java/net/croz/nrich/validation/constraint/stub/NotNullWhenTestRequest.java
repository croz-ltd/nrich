package net.croz.nrich.validation.constraint.stub;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.croz.nrich.validation.api.constraint.NotNullWhen;

import java.util.function.Predicate;

@RequiredArgsConstructor
@Getter
@NotNullWhen(property = "property", condition = NotNullWhenTestRequest.Condition.class)
public class NotNullWhenTestRequest {

    private final String property;

    private final String differentProperty;

    public static class Condition implements Predicate<NotNullWhenTestRequest> {
        @Override
        public boolean test(final NotNullWhenTestRequest notNullWhenTestRequest) {
            return "not null".equals(notNullWhenTestRequest.getDifferentProperty());
        }
    }
}
