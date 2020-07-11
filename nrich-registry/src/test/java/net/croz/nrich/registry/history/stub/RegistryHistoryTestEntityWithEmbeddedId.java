package net.croz.nrich.registry.history.stub;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;

import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Audited
@Entity
public class RegistryHistoryTestEntityWithEmbeddedId {

    @EmbeddedId
    private RegistryHistoryTestEntityWithEmbeddedIdPrimaryKey id;

    private BigDecimal amount;

    @NoArgsConstructor
    @AllArgsConstructor
    @Setter
    @Getter
    @EqualsAndHashCode
    @Embeddable
    public static class RegistryHistoryTestEntityWithEmbeddedIdPrimaryKey implements Serializable {

        private Long firstId;

        private Long secondId;

        public Map<String, Long> asMap() {
            final Map<String, Long> mapKey = new HashMap<>();

            mapKey.put("firstId", firstId);
            mapKey.put("secondId", secondId);

            return mapKey;
        }

    }
}
