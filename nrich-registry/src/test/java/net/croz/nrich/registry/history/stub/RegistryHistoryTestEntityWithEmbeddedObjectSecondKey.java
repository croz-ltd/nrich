package net.croz.nrich.registry.history.stub;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Setter
@Getter
@Audited
@Entity
public class RegistryHistoryTestEntityWithEmbeddedObjectSecondKey {

    @GeneratedValue
    @Id
    private Long id;

}
