package net.croz.nrich.search.model;

import lombok.Data;

@Data
public class SearchPropertyJoin {

    private final String parentProperty;

    private final String childProperty;

    public static SearchPropertyJoin defaultJoinById() {
        return new SearchPropertyJoin("id", "id");
    }

}
