package net.croz.nrich.search.api.model.property;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class SearchPropertyJoin {

    private final String parentProperty;

    private final String childProperty;

    public static SearchPropertyJoin defaultJoinById() {
        return new SearchPropertyJoin("id", "id");
    }

}