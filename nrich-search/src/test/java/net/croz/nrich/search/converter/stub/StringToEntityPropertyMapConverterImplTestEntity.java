package net.croz.nrich.search.converter.stub;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.util.Date;

@Setter
@Getter
@Entity
public class StringToEntityPropertyMapConverterImplTestEntity {

    @Id
    private Long id;

    private String name;

    private Date date;

    private Integer value;

    @OneToOne
    private StringToEntityPropertyMapConverterImplTestNestedEntity nestedEntity;

}
