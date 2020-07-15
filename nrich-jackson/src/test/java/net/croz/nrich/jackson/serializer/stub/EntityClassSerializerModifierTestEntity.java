package net.croz.nrich.jackson.serializer.stub;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;

@Entity
@Setter
@Getter
public class EntityClassSerializerModifierTestEntity {

    private String name;

}
