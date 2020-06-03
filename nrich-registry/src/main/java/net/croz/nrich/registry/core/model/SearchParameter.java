package net.croz.nrich.registry.data.model;

import lombok.Data;

import java.util.List;

@Data
public class SearchParameter {

    private List<String> propertyNameList;

    private String query;

}
