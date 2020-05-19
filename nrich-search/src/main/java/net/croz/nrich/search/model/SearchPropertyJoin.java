package net.croz.nrich.search.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SearchPropertyJoin {

    private String parentProperty;

    private String childProperty;

    public static SearchPropertyJoin defaultJoinById() {
        return new SearchPropertyJoin("id", "id");
    }

}
