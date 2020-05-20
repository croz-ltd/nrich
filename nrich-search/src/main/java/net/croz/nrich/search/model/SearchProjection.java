package net.croz.nrich.search.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.function.Function;

@RequiredArgsConstructor
@Builder
@AllArgsConstructor
@Getter
public class SearchProjection<R> {

    private final String path;

    private String alias;

    private Function<R, Boolean> condition;

}
