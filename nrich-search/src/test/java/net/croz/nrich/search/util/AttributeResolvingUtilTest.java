package net.croz.nrich.search.util;

import org.hibernate.metamodel.model.domain.internal.AbstractManagedType;
import org.hibernate.query.sqm.tree.domain.SqmPersistentAttribute;
import org.junit.jupiter.api.Test;

import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.ManagedType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

class AttributeResolvingUtilTest {

    private static final String ATTRIBUTE_NAME = "attributeName";

    private static final Attribute<?, ?> ATTRIBUTE_VALUE = mock();

    private static final SqmPersistentAttribute<?, ?> PERSISTENT_ATTRIBUTE_VALUE = mock();

    @Test
    void shouldResolveAttributeFromManagedType() {
        // given
        ManagedType<?> managedType = mock(ManagedType.class);
        doReturn(ATTRIBUTE_VALUE).when(managedType).getAttribute(ATTRIBUTE_NAME);

        // when
        Attribute<?, ?> result = AttributeResolvingUtil.resolveAttributeByName(managedType, ATTRIBUTE_NAME);

        // then
        assertThat(result).isEqualTo(ATTRIBUTE_VALUE);
    }

    @Test
    void shouldReturnNullIfAttributeDoesNotExist() {
        // given
        ManagedType<?> managedType = mock(ManagedType.class);
        doThrow(IllegalArgumentException.class).when(managedType).getAttribute(ATTRIBUTE_NAME);

        // when
        Attribute<?, ?> result = AttributeResolvingUtil.resolveAttributeByName(managedType, ATTRIBUTE_NAME);

        // then
        assertThat(result).isNull();
    }

    @Test
    void shouldResolveAttributeFromAbstractManagedType() {
        // given
        AbstractManagedType<?> managedType = mock(AbstractManagedType.class);
        doReturn(PERSISTENT_ATTRIBUTE_VALUE).when(managedType).findAttribute(ATTRIBUTE_NAME);

        // when
        Attribute<?, ?> result = AttributeResolvingUtil.resolveAttributeByName(managedType, ATTRIBUTE_NAME);

        // then
        assertThat(result).isEqualTo(PERSISTENT_ATTRIBUTE_VALUE);
    }

    @Test
    void shouldResolveSubclassAttributeFromAbstractManagedType() {
        // given
        AbstractManagedType<?> managedType = mock(AbstractManagedType.class);
        doReturn(PERSISTENT_ATTRIBUTE_VALUE).when(managedType).findSubTypesAttribute(ATTRIBUTE_NAME);

        // when
        Attribute<?, ?> result = AttributeResolvingUtil.resolveAttributeByName(managedType, ATTRIBUTE_NAME);

        // then
        assertThat(result).isEqualTo(PERSISTENT_ATTRIBUTE_VALUE);
    }
}
