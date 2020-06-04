package net.croz.nrich.registry.data.stub;

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
import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Audited
@Entity
public class RegistryTestEntityWithEmbeddedId {

    @EmbeddedId
    private RegistryTestEntityWithEmbeddedIdPrimaryKey id;

    private String name;

    @NoArgsConstructor
    @AllArgsConstructor
    @Setter
    @Getter
    @EqualsAndHashCode
    @Embeddable
    public static class RegistryTestEntityWithEmbeddedIdPrimaryKey implements Serializable {

        private Long firstId;

        private Long secondId;

        public Map<String, Long> asMap() {
            final Map<String, Long> mapKey = new HashMap<>();

            mapKey.put("id.firstId", firstId);
            mapKey.put("id.secondId", secondId);

            return mapKey;
        }

    }
}
