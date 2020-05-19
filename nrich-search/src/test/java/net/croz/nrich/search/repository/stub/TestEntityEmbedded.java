package net.croz.nrich.search.repository.stub;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.persistence.Entity;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Embeddable
public class TestEntityEmbedded {

    private String embeddedName;

}
