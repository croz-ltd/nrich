package net.croz.nrich.registry.configuration.service.stub;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class RegistryConfigurationTestEntityWithAssociationAndEmbeddedId {

    @EmbeddedId
    private RegistryConfigurationTestEntityPrimaryKey id;

    private BigDecimal amount;

    @OneToOne
    private RegistryConfigurationTestEntity registryConfigurationTestEntity;

    @OneToMany
    private List<RegistryConfigurationTestEntity> registryConfigurationTestEntityList;

    @Setter
    @Getter
    @Embeddable
    static class RegistryConfigurationTestEntityPrimaryKey implements Serializable {

        private Long firstId;

        private Long secondId;

    }
}
