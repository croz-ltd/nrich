package net.croz.nrich.search.repository.stub;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Setter
@Getter
@Entity
public class TestNestedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String nestedEntityName;

    private String nestedEntityAliasName;

    @OneToOne(cascade = CascadeType.ALL)
    private TestDoubleNestedEntity doubleNestedEntity;

}
