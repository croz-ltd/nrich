package net.croz.nrich.search.repository.stub;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;

@Setter
@Getter
@Embeddable
public class TestEntityEmbedded {

    private String embeddedName;

}
