package net.croz.nrich.search.util;

import java.util.List;

public final class QueryUtil {

    private QueryUtil() {
    }

    public static Long toCountResult(List<Long> results) {
        return results.stream().mapToLong(value -> value == null ? 0L : value).sum();
    }
}
