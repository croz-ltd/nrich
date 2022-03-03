package net.croz.nrich.registry.configuration.stub;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
@Entity
public class RegistryConfigurationTestEntityWithAssociationAndEmbeddedId {

    @EmbeddedId
    private RegistryConfigurationTestEntityPrimaryKey id;

    private BigDecimal amount;

    @OneToOne
    private RegistryConfigurationTestEntity registryConfigurationTestEntityOneToOne;

    @ManyToOne
    private RegistryConfigurationTestEntity registryConfigurationTestEntityManyToOne;

    @OneToMany
    private List<RegistryConfigurationTestEntity> registryConfigurationTestEntityList;

    @Setter
    @Getter
    @EqualsAndHashCode
    @Embeddable
    static class RegistryConfigurationTestEntityPrimaryKey implements Serializable {

        private Long firstId;

        private Long secondId;

    }
}
