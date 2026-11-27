package net.croz.nrich.search.repository.stub;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import java.util.List;

@Setter
@Getter
@Entity
public class TestSubEntity extends TestEntity {

    private String subName;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TestSubEntity> subEntityList;

}
