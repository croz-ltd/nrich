package net.croz.nrich.search.repository.stub;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class TestEntityWithEmbeddedId {

    @EmbeddedId
    private TestEntityWithEmbeddedIdObjectId id;

    private String name;

    @NoArgsConstructor
    @AllArgsConstructor
    @Setter
    @Getter
    @EqualsAndHashCode
    @Embeddable
    public static class TestEntityWithEmbeddedIdObjectId implements Serializable {

        @ManyToOne
        private RegistryHistoryTestEntityWithEmbeddedObjectFirstKey firstKey;

        @ManyToOne
        private RegistryHistoryTestEntityWithEmbeddedObjectSecondKey secondKey;

    }

    @Setter
    @Getter
    @Entity
    public static class RegistryHistoryTestEntityWithEmbeddedObjectFirstKey {

        @GeneratedValue
        @Id
        private Long id;

    }

    @Setter
    @Getter
    @Entity
    public static class RegistryHistoryTestEntityWithEmbeddedObjectSecondKey {

        @GeneratedValue
        @Id
        private Long id;

    }
}
