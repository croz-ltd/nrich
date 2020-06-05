package net.croz.nrich.registry.history.stub;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionListener;

import javax.persistence.Entity;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@RevisionEntity(RegistryHistoryTestRevisionEntity.CustomRevisionListener.class)
@Entity
public class RegistryHistoryTestRevisionEntity extends DefaultRevisionEntity {

    private String revisionProperty;


    static class CustomRevisionListener implements RevisionListener {

        @Override
        public void newRevision(final Object revisionEntity) {
            final RegistryHistoryTestRevisionEntity registryHistoryTestRevisionEntity = (RegistryHistoryTestRevisionEntity) revisionEntity;

            registryHistoryTestRevisionEntity.revisionProperty = "revision property value";
        }
    }
}
