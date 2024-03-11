package net.croz.nrich.search.repository.stub;

import jakarta.persistence.Entity;

import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class TestSubEntity extends TestEntity {

    String subName;

}
