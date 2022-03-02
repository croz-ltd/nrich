package net.croz.nrich.registry.history.stub;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionListener;

import javax.persistence.Entity;

@Setter
@Getter
@RevisionEntity(RegistryHistoryTestRevisionEntity.CustomRevisionListener.class)
@Entity
public class RegistryHistoryTestRevisionEntity extends DefaultRevisionEntity {

    private String revisionProperty;

    static class CustomRevisionListener implements RevisionListener {

        @Override
        public void newRevision(Object revisionEntity) {
            RegistryHistoryTestRevisionEntity registryHistoryTestRevisionEntity = (RegistryHistoryTestRevisionEntity) revisionEntity;

            registryHistoryTestRevisionEntity.revisionProperty = "revision property value";
        }
    }
}
