package net.croz.nrich.registry.configuration.stub;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import java.io.Serializable;

@Setter
@Getter
@IdClass(RegistryConfigurationTestEntityWithIdClass.RegistryConfigurationTestEntityWithIdClassId.class)
@Entity
public class RegistryConfigurationTestEntityWithIdClass {

    @Id
    private Long firstId;

    @Id
    private Long secondId;

    private String name;

    @Setter
    @Getter
    @EqualsAndHashCode
    static class RegistryConfigurationTestEntityWithIdClassId implements Serializable {

        private Long firstId;

        private Long secondId;

    }
}
