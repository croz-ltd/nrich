package net.croz.nrich.security.csrf.core.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CsrfExcludeConfig {

    private String regex;

    private String uri;

}
