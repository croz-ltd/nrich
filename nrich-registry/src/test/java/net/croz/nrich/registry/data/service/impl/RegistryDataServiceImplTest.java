package net.croz.nrich.registry.data.service.impl;

import net.croz.nrich.registry.RegistryTestConfiguration;
import net.croz.nrich.registry.data.request.BulkListRegistryRequest;
import net.croz.nrich.registry.data.request.CreateRegistryServiceRequest;
import net.croz.nrich.registry.data.request.DeleteRegistryRequest;
import net.croz.nrich.registry.data.request.ListRegistryRequest;
import net.croz.nrich.registry.data.request.UpdateRegistryServiceRequest;
import net.croz.nrich.registry.data.service.RegistryDataService;
import net.croz.nrich.registry.data.stub.RegistryTestEmbeddedUserGroup;
import net.croz.nrich.registry.data.stub.RegistryTestEmbeddedUserGroupId;
import net.croz.nrich.registry.data.stub.RegistryTestEntity;
import net.croz.nrich.registry.data.stub.RegistryTestEntityWithOverriddenSearchConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.Map;

import static net.croz.nrich.registry.data.testutil.RegistryDataGeneratingUtil.createBulkListRegistryRequest;
import static net.croz.nrich.registry.data.testutil.RegistryDataGeneratingUtil.createDeleteEmbeddedUserGroupRequest;
import static net.croz.nrich.registry.data.testutil.RegistryDataGeneratingUtil.createDeleteRegistryRequest;
import static net.croz.nrich.registry.data.testutil.RegistryDataGeneratingUtil.createListRegistryRequest;
import static net.croz.nrich.registry.data.testutil.RegistryDataGeneratingUtil.createRegistryServiceRequest;
import static net.croz.nrich.registry.data.testutil.RegistryDataGeneratingUtil.createRegistryTestEmbeddedUserGroup;
import static net.croz.nrich.registry.data.testutil.RegistryDataGeneratingUtil.createRegistryTestEmbeddedUserGroupId;
import static net.croz.nrich.registry.data.testutil.RegistryDataGeneratingUtil.createRegistryTestEntity;
import static net.croz.nrich.registry.data.testutil.RegistryDataGeneratingUtil.createRegistryTestEntityList;
import static net.croz.nrich.registry.data.testutil.RegistryDataGeneratingUtil.createRegistryTestEntityWithOverriddenSearchConfigurationList;
import static net.croz.nrich.registry.data.testutil.RegistryDataGeneratingUtil.createUpdateEmbeddedUserGroupRequest;
import static net.croz.nrich.registry.data.testutil.RegistryDataGeneratingUtil.updateRegistryServiceRequest;
import static net.croz.nrich.registry.testutil.PersistenceTestUtil.flushEntityManager;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@Transactional
@SpringJUnitWebConfig(RegistryTestConfiguration.class)
public class RegistryDataServiceImplTest {

    @Autowired
    private RegistryDataService registryDataService;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void shouldBulkListRegistry() {
        // given
        createRegistryTestEntityList(entityManager);

        final BulkListRegistryRequest request = createBulkListRegistryRequest(RegistryTestEntity.class.getName(), "name%");

        // when
        final Map<String, Page<RegistryTestEntity>> result = registryDataService.bulkList(request);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(RegistryTestEntity.class.getName())).hasSize(5);
    }

    @Test
    void shouldListRegistry() {
        // given
        createRegistryTestEntityList(entityManager);

        final ListRegistryRequest request = createListRegistryRequest(RegistryTestEntity.class.getName(), "name%");

        // when
        final Page<RegistryTestEntity> result = registryDataService.list(request);

        // then
        assertThat(result).hasSize(5);
    }

    @Test
    void shouldListRegistryWithPaging() {
        // given
        createRegistryTestEntityList(entityManager);

        final ListRegistryRequest request = createListRegistryRequest(RegistryTestEntity.class.getName(), "name%", 0, 3);

        // when
        final Page<RegistryTestEntity> result = registryDataService.list(request);

        // then
        assertThat(result).hasSize(3);
        assertThat(result.getTotalPages()).isEqualTo(2);
        assertThat(result.getTotalElements()).isEqualTo(5);
    }

    @Test
    void shouldReturnAllElementsOnEmptySearchParameters() {
        // given
        createRegistryTestEntityList(entityManager);

        final ListRegistryRequest request = createListRegistryRequest(RegistryTestEntity.class.getName(), null);

        // when
        final Page<RegistryTestEntity> result = registryDataService.list(request);

        // then
        assertThat(result).hasSize(5);
    }

    @Test
    void shouldCreateRegistryEntity() {
        // given
        final CreateRegistryServiceRequest request = createRegistryServiceRequest(RegistryTestEntity.class.getName());

        // when
        final RegistryTestEntity registryTestEntity = registryDataService.create(request);

        // then
        assertThat(registryTestEntity).isNotNull();

        // and when
        flushEntityManager(entityManager);
        final RegistryTestEntity loaded = entityManager.find(RegistryTestEntity.class, registryTestEntity.getId());

        // then
        assertThat(loaded.getAge()).isEqualTo(50);
        assertThat(loaded.getName()).isEqualTo("name 1");
    }

    @Test
    void shouldUpdateRegistryEntity() {
        // given
        final RegistryTestEntity registryTestEntity = createRegistryTestEntity(entityManager);

        final UpdateRegistryServiceRequest request = updateRegistryServiceRequest(RegistryTestEntity.class.getName(), registryTestEntity.getId());

        // when
        final RegistryTestEntity updatedEntity = registryDataService.update(request);

        // then
        assertThat(updatedEntity).isNotNull();

        // and when
        flushEntityManager(entityManager);
        final RegistryTestEntity loaded = entityManager.find(RegistryTestEntity.class, registryTestEntity.getId());

        // then
        assertThat(loaded.getAge()).isEqualTo(51);
        assertThat(loaded.getName()).isEqualTo("name 2");
    }

    @Test
    void shouldUpdateRegistryEntityWithCompositeKey() {
        // given
        final String joinedPropertyUpdateValue = "updated joined property";
        final RegistryTestEmbeddedUserGroup registryTestEmbeddedUserGroup = createRegistryTestEmbeddedUserGroup(entityManager);
        final RegistryTestEmbeddedUserGroupId registryUpdateGroupId = createRegistryTestEmbeddedUserGroupId(entityManager);

        final UpdateRegistryServiceRequest request = createUpdateEmbeddedUserGroupRequest(registryTestEmbeddedUserGroup.getUserGroupId(), registryUpdateGroupId, joinedPropertyUpdateValue);

        // when
        final RegistryTestEmbeddedUserGroup result = registryDataService.update(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getJoinedPropertyValue()).isEqualTo(joinedPropertyUpdateValue);

        // and when
        flushEntityManager(entityManager);
        final RegistryTestEmbeddedUserGroup loaded = findRegistryTestEmbeddedUserGroup(registryUpdateGroupId);

        // then
        assertThat(loaded).isNotNull();
    }

    @Test
    void shouldDeleteRegistryEntity() {
        // given
        final RegistryTestEntity registryTestEntity = createRegistryTestEntity(entityManager);

        final DeleteRegistryRequest request = createDeleteRegistryRequest(RegistryTestEntity.class.getName(), registryTestEntity.getId());

        // when
        final RegistryTestEntity result = registryDataService.delete(request);

        // then
        assertThat(result.getId()).isEqualTo(registryTestEntity.getId());
    }

    @Test
    void shouldDeleteRegistryEntityWithCompositeKey() {
        // given
        final RegistryTestEmbeddedUserGroup registryTestEmbeddedUserGroup = createRegistryTestEmbeddedUserGroup(entityManager);

        final DeleteRegistryRequest request = createDeleteEmbeddedUserGroupRequest(registryTestEmbeddedUserGroup.getUserGroupId());

        // when
        final RegistryTestEmbeddedUserGroup result = registryDataService.delete(request);

        // then
        assertThat(result).isNotNull();

        // and when
        flushEntityManager(entityManager);
        final RegistryTestEmbeddedUserGroup loaded = findRegistryTestEmbeddedUserGroup(registryTestEmbeddedUserGroup.getUserGroupId());

        // then
        assertThat(loaded).isNull();
    }

    @Test
    void shouldThrowExceptionOnInvalidPrimaryKey() {
        // given
        final RegistryTestEmbeddedUserGroup registryTestEmbeddedUserGroup = createRegistryTestEmbeddedUserGroup(entityManager);

        final DeleteRegistryRequest request = createDeleteEmbeddedUserGroupRequest(registryTestEmbeddedUserGroup.getUserGroupId());
        request.setId(new Object());

        // when
        final Throwable thrown = catchThrowable(() -> registryDataService.delete(request));

        // then
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldListRegistryWithOverriddenSearchConfiguration() {
        // given
        createRegistryTestEntityWithOverriddenSearchConfigurationList(entityManager);

        final ListRegistryRequest request = createListRegistryRequest(RegistryTestEntityWithOverriddenSearchConfiguration.class.getName(), "name%");

        // when
        final Page<RegistryTestEntity> result = registryDataService.list(request);

        // then
        assertThat(result).isEmpty();

        // and when
        final ListRegistryRequest requestWithEqualName = createListRegistryRequest(RegistryTestEntityWithOverriddenSearchConfiguration.class.getName(), "name 0");

        final Page<RegistryTestEntity> resultWithEqualName = registryDataService.list(requestWithEqualName);

        // then
        assertThat(resultWithEqualName).hasSize(1);
    }

    private RegistryTestEmbeddedUserGroup findRegistryTestEmbeddedUserGroup(final RegistryTestEmbeddedUserGroupId groupId) {
        try {
            return (RegistryTestEmbeddedUserGroup) entityManager.createQuery("from RegistryTestEmbeddedUserGroup where userGroupId.user.id = :userId and userGroupId.group.id = :groupId")
                    .setParameter("userId", groupId.getUser().getId())
                    .setParameter("groupId", groupId.getGroup().getId())
                    .getSingleResult();
        }
        catch (final NoResultException ignored) {
            return null;
        }
    }
}
