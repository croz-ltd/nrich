package net.croz.nrich.jackson.serializer.stub;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;

@Setter
@Getter
@Entity
public class EntityClassSerializerModifierTestEntity {

    private String name;

}
