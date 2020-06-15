package net.croz.nrich.search.api.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class SearchPropertyMapping {

    private final String name;

    private final String path;

}
