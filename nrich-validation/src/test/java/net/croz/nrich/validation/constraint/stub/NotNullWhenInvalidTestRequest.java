package net.croz.nrich.validation.constraint.stub;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.croz.nrich.validation.api.constraint.NotNullWhen;

import java.util.function.Predicate;

@RequiredArgsConstructor
@Getter
@NotNullWhen(property = "nonExistingProperty", condition = NotNullWhenInvalidTestRequest.Condition.class)
public class NotNullWhenInvalidTestRequest {

    private final String property;

    private final String differentProperty;

    public static class Condition implements Predicate<NotNullWhenInvalidTestRequest> {
        @Override
        public boolean test(final NotNullWhenInvalidTestRequest notNullWhenTestRequest) {
            return true;
        }
    }
}
