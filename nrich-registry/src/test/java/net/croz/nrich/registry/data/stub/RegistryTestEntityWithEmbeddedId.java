package net.croz.nrich.registry.data.stub;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
@Audited
@Entity
public class RegistryTestEntityWithEmbeddedId {

    @EmbeddedId
    private RegistryTestEntityWithEmbeddedIdPrimaryKey id;

    private String name;

    @Setter
    @Getter
    @EqualsAndHashCode
    @Embeddable
    public static class RegistryTestEntityWithEmbeddedIdPrimaryKey implements Serializable {

        private Long firstId;

        private Long secondId;

        public Map<String, Object> asMap() {
            Map<String, Object> mapKey = new HashMap<>();

            mapKey.put("firstId", firstId.intValue());
            mapKey.put("secondId", secondId.intValue());

            return mapKey;
        }

    }
}
