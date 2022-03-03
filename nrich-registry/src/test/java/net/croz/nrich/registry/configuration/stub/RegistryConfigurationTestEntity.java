package net.croz.nrich.registry.configuration.stub;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;

@Setter
@Getter
@Entity
public class RegistryConfigurationTestEntity {

    @Id
    private Long id;

    private String name;

    private String nonEditableProperty;

    private String skippedProperty;

    private Float floatNumber;

    private Double doubleNumber;

}
