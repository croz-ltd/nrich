package net.croz.nrich.registry.core.model;

import lombok.Data;

import java.util.List;

@Data
public class SearchParameter {

    private List<String> propertyNameList;

    private String query;

}
