package net.croz.nrich.search.converter.stub;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;

@Setter
@Getter
@Entity
class DefaultStringToEntityPropertyMapConverterTestNestedEntity {

    @Id
    private Long id;

    private String nestedName;

}
