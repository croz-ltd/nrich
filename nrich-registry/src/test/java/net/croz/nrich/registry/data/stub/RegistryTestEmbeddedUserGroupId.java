package net.croz.nrich.registry.data.stub;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.util.Objects;

@Setter
@Getter
@Embeddable
public class RegistryTestEmbeddedUserGroupId implements Serializable {

    @ManyToOne
    private RegistryTestEmbeddedUser user;

    @ManyToOne
    private RegistryTestEmbeddedGroup group;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        RegistryTestEmbeddedUserGroupId that = (RegistryTestEmbeddedUserGroupId) o;

        return user.getId().equals(that.user.getId()) && group.getId().equals(that.group.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(user.getId(), group.getId());
    }
}
