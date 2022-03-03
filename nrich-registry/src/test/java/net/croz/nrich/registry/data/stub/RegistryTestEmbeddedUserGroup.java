package net.croz.nrich.registry.data.stub;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@Setter
@Getter
@Entity
public class RegistryTestEmbeddedUserGroup {

    @EmbeddedId
    private RegistryTestEmbeddedUserGroupId userGroupId;

    private String joinedPropertyValue;

}
