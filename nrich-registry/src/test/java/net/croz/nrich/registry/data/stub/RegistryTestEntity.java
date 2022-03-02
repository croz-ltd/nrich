package net.croz.nrich.registry.data.stub;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Setter
@Getter
@Entity
public class RegistryTestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotNull
    private String name;

    @NotNull
    private Integer age;

    @ManyToOne(fetch = FetchType.LAZY)
    private RegistryTestEntity parent;

}
