package net.croz.nrich.registry.security.stub;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;

@Setter
@Getter
@Entity
public class RegistryConfigurationUpdateInterceptorTestEntity {

    @Id
    private Long id;

    private String name;

}
