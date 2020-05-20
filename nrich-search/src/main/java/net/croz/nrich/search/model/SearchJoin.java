package net.croz.nrich.search.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.persistence.criteria.JoinType;
import java.util.function.Function;

@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class SearchJoin<R> {

    private final String path;

    private String alias;

    private JoinType joinType;

    private Function<R, Boolean> condition;

    public static <R> SearchJoin<R> innerJoin(final String path) {
        return new SearchJoin<>(path, path, JoinType.INNER, null);
    }
}
