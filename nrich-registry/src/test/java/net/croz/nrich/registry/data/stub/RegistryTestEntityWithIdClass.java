package net.croz.nrich.registry.data.stub;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@IdClass(RegistryTestEntityWithIdClass.RegistryConfigurationTestEntityWithIdClassId.class)
@Entity
public class RegistryTestEntityWithIdClass {

    @Id
    private Long firstId;

    @Id
    private Long secondId;

    private String name;

    @Getter
    @Setter
    @EqualsAndHashCode
    static class RegistryConfigurationTestEntityWithIdClassId implements Serializable {

        private Long firstId;

        private Long secondId;

    }
}
