package net.croz.nrich.registry.data.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class SearchParameter {

    private List<String> propertyNameList;

    private String query;

}
