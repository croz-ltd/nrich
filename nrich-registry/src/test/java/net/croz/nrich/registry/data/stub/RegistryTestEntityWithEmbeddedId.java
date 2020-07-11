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

        public Map<String, Object> asMap() {
            final Map<String, Object> mapKey = new HashMap<>();

            mapKey.put("firstId", firstId.intValue());
            mapKey.put("secondId", secondId.intValue());

            return mapKey;
        }

    }
}
