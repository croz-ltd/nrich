package net.croz.nrich.search.repository.stub;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDate;

@Setter
@Getter
@Entity
public class TestStringSearchEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String name;

    private Integer age;

    private TestEntityEnum testEntityEnum;

    private LocalDate localDate;

}
