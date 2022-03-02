package net.croz.nrich.registry.data.stub;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@Setter
@Getter
@Entity
public class RegistryTestEntityWithDifferentIdName {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long registryTestEntityId;

    @NotNull
    private String name;

}
