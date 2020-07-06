package net.croz.nrich.registry.api.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class RegistryCategoryDefinitionConfiguration {

    private String registryCategoryId;

    private List<String> includeEntityPatternList;

    private List<String> excludeEntityPatternList;

}
