package net.croz.nrich.registry.data.stub;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Embeddable
public class RegistryTestEmbeddedUserGroupId implements Serializable {

    @ManyToOne
    private RegistryTestEmbeddedUser user;

    @ManyToOne
    private RegistryTestEmbeddedGroup group;

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final RegistryTestEmbeddedUserGroupId that = (RegistryTestEmbeddedUserGroupId) o;

        return user.getId().equals(that.user.getId()) && group.getId().equals(that.group.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(user.getId(), group.getId());
    }
}
