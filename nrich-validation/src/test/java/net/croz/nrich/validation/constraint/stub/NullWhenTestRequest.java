package net.croz.nrich.validation.constraint.stub;

import lombok.Data;
import net.croz.nrich.validation.api.constraint.NullWhen;

import java.util.function.Predicate;

@Data
@NullWhen(property = "property", condition = NullWhenTestRequest.Condition.class)
public class NullWhenTestRequest {

    private final String property;

    private final String differentProperty;

    public static class Condition implements Predicate<NullWhenTestRequest> {
        @Override
        public boolean test(final NullWhenTestRequest notNullWhenTestRequest) {
            return "not null".equals(notNullWhenTestRequest.getDifferentProperty());
        }
    }
}
