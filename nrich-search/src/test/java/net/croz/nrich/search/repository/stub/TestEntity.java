package net.croz.nrich.search.repository.stub;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.util.List;

@Setter
@Getter
@Entity
public class TestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String name;

    private Integer age;

    @OneToOne(cascade = CascadeType.ALL)
    private TestNestedEntity nestedEntity;

    @JoinColumn
    @OneToMany(cascade = CascadeType.ALL)
    private List<TestCollectionEntity> collectionEntityList;

    private TestEntityEnum testEntityEnum;

    @Embedded
    private TestEntityEmbedded testEntityEmbedded;

}
