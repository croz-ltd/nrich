package net.croz.nrich.registry.history.stub;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
@Audited
@Entity
public class RegistryHistoryTestEntityWithEmbeddedObject {

    @EmbeddedId
    private RegistryHistoryTestEntityWithEmbeddedObjectId id;

    private BigDecimal amount;

    @Setter
    @Getter
    @EqualsAndHashCode
    @Embeddable
    public static class RegistryHistoryTestEntityWithEmbeddedObjectId implements Serializable {

        @ManyToOne
        private RegistryHistoryTestEntityWithEmbeddedObjectFirstKey firstKey;

        @ManyToOne
        private RegistryHistoryTestEntityWithEmbeddedObjectSecondKey secondKey;

        public Map<String, Object> asMap() {
            Map<String, Object> mapKey = new HashMap<>();

            mapKey.put("firstKey", firstKey);
            mapKey.put("secondKey", secondKey);

            return mapKey;
        }

    }
}
