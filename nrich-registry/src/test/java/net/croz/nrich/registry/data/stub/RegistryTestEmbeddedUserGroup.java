package net.croz.nrich.registry.data.stub;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class RegistryTestEmbeddedUserGroup {

    @EmbeddedId
    private RegistryTestEmbeddedUserGroupId userGroupId;

    private String joinedPropertyValue;

}
