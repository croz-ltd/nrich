package net.croz.nrich.registry.configuration.service.stub;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class RegistryConfigurationTestEntity {

    @Id
    private Long id;

    private String name;

    private String nonEditableProperty;

    private String skippedProperty;

}
