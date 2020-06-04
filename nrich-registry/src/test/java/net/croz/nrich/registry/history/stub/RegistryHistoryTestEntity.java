package net.croz.nrich.registry.history.stub;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Audited
@Entity
public class RegistryHistoryTestEntity {

    @GeneratedValue
    @Id
    private Long id;

    private String name;

    @OneToOne(cascade = CascadeType.ALL)
    private RegistryHistoryTestEntity parent;

}
