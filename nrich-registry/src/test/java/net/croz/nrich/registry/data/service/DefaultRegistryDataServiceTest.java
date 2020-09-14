package net.croz.nrich.registry.data.service;

import net.croz.nrich.registry.RegistryTestConfiguration;
import net.croz.nrich.registry.api.data.request.ListBulkRegistryRequest;
import net.croz.nrich.registry.api.data.request.ListRegistryRequest;
import net.croz.nrich.registry.api.data.service.RegistryDataService;
import net.croz.nrich.registry.data.stub.CreateRegistryTestEntityRequest;
import net.croz.nrich.registry.data.stub.RegistryTestEmbeddedUserGroup;
import net.croz.nrich.registry.data.stub.RegistryTestEmbeddedUserGroupId;
import net.croz.nrich.registry.data.stub.RegistryTestEntity;
import net.croz.nrich.registry.data.stub.RegistryTestEntityWithDifferentIdName;
import net.croz.nrich.registry.data.stub.RegistryTestEntityWithEmbeddedId;
import net.croz.nrich.registry.data.stub.RegistryTestEntityWithIdClass;
import net.croz.nrich.registry.data.stub.RegistryTestEntityWithOverriddenSearchConfiguration;
import net.croz.nrich.registry.data.stub.RegistryTestEntityWithoutAssociation;
import net.croz.nrich.registry.data.stub.UpdateRegistryTestEntityRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Map;

import static net.croz.nrich.registry.data.testutil.RegistryDataGeneratingUtil.createBulkListRegistryRequest;
import static net.croz.nrich.registry.data.testutil.RegistryDataGeneratingUtil.createListRegistryRequest;
import static net.croz.nrich.registry.data.testutil.RegistryDataGeneratingUtil.createRegistryTestEmbeddedUserGroup;
import static net.croz.nrich.registry.data.testutil.RegistryDataGeneratingUtil.createRegistryTestEmbeddedUserGroupId;
import static net.croz.nrich.registry.data.testutil.RegistryDataGeneratingUtil.createRegistryTestEntity;
import static net.croz.nrich.registry.data.testutil.RegistryDataGeneratingUtil.createRegistryTestEntityList;
import static net.croz.nrich.registry.data.testutil.RegistryDataGeneratingUtil.createRegistryTestEntityWithDifferentIdNameList;
import static net.croz.nrich.registry.data.testutil.RegistryDataGeneratingUtil.createRegistryTestEntityWithEmbeddedId;
import static net.croz.nrich.registry.data.testutil.RegistryDataGeneratingUtil.createRegistryTestEntityWithIdClass;
import static net.croz.nrich.registry.data.testutil.RegistryDataGeneratingUtil.createRegistryTestEntityWithOverriddenSearchConfigurationList;
import static net.croz.nrich.registry.data.testutil.RegistryDataGeneratingUtil.createRegistryTestEntityWithoutAssociation;
import static net.croz.nrich.registry.data.testutil.RegistryDataGeneratingUtil.registryTestEntityWithIdClassId;
import static net.croz.nrich.registry.data.testutil.RegistryDataResolvingUtil.findRegistryTestEmbeddedUserGroup;
import static net.croz.nrich.registry.data.testutil.RegistryDataResolvingUtil.findRegistryTestEntityWithIdClass;
import static net.croz.nrich.registry.testutil.PersistenceTestUtil.flushEntityManager;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@Transactional
@SpringJUnitWebConfig(RegistryTestConfiguration.class)
class DefaultRegistryDataServiceTest {

    @Autowired
    private RegistryDataService registryDataService;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void shouldBulkListRegistry() {
        // given
        createRegistryTestEntityList(entityManager);

        final ListBulkRegistryRequest request = createBulkListRegistryRequest(RegistryTestEntity.class.getName(), "name%");

        // when
        final Map<String, Page<?>> result = registryDataService.listBulk(request);

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
    void shouldListRegistryEntityWithDifferentIdName() {
        // given
        createRegistryTestEntityWithDifferentIdNameList(entityManager);

        final ListRegistryRequest request = createListRegistryRequest(RegistryTestEntityWithDifferentIdName.class.getName(), "name%");

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
        // when
        final RegistryTestEntity registryTestEntity = registryDataService.create(RegistryTestEntity.class.getName(), new CreateRegistryTestEntityRequest("name 1", 50));

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

        // when
        final RegistryTestEntity updatedEntity = registryDataService.update(RegistryTestEntity.class.getName(), registryTestEntity.getId(), new UpdateRegistryTestEntityRequest(100L, "name 2", 51));

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
    void shouldUpdateRegistryEntityWithoutAssociations() {
        // given
        final RegistryTestEntityWithoutAssociation registryTestEntity = createRegistryTestEntityWithoutAssociation(entityManager);

        // when
        final RegistryTestEntityWithoutAssociation updatedEntity = registryDataService.update(RegistryTestEntityWithoutAssociation.class.getName(), registryTestEntity.getId(), new UpdateRegistryTestEntityRequest(100L, "name 2", 51));

        // then
        assertThat(updatedEntity).isNotNull();

        // and when
        flushEntityManager(entityManager);
        final RegistryTestEntityWithoutAssociation loaded = entityManager.find(RegistryTestEntityWithoutAssociation.class, registryTestEntity.getId());

        // then
        assertThat(loaded.getAge()).isEqualTo(51);
        assertThat(loaded.getName()).isEqualTo("name 2");
    }

    @Test
    void shouldUpdateRegistryEntityWithEmbeddedObjectId() {
        // given
        final String joinedPropertyUpdateValue = "updated joined property";
        final RegistryTestEmbeddedUserGroup registryTestEmbeddedUserGroup = createRegistryTestEmbeddedUserGroup(entityManager);
        final RegistryTestEmbeddedUserGroupId registryUpdateGroupId = createRegistryTestEmbeddedUserGroupId(entityManager);

        // when
        final RegistryTestEmbeddedUserGroup result = registryDataService.update(RegistryTestEmbeddedUserGroup.class.getName(), registryTestEmbeddedUserGroup.getUserGroupId(), new RegistryTestEmbeddedUserGroup(registryUpdateGroupId, joinedPropertyUpdateValue));

        // then
        assertThat(result).isNotNull();
        assertThat(result.getJoinedPropertyValue()).isEqualTo(joinedPropertyUpdateValue);

        // and when
        flushEntityManager(entityManager);
        final RegistryTestEmbeddedUserGroup loaded = findRegistryTestEmbeddedUserGroup(entityManager, registryUpdateGroupId);

        // then
        assertThat(loaded).isNotNull();
    }

    @Test
    void shouldDeleteRegistryEntity() {
        // given
        final RegistryTestEntity registryTestEntity = createRegistryTestEntity(entityManager);

        // when
        final RegistryTestEntity result = registryDataService.delete(RegistryTestEntity.class.getName(), registryTestEntity.getId());

        // then
        assertThat(result.getId()).isEqualTo(registryTestEntity.getId());
    }

    @Test
    void shouldDeleteRegistryEntityWithEmbeddedObjectId() {
        // given
        final RegistryTestEmbeddedUserGroup registryTestEmbeddedUserGroup = createRegistryTestEmbeddedUserGroup(entityManager);

        // when
        final RegistryTestEmbeddedUserGroup result = registryDataService.delete(RegistryTestEmbeddedUserGroup.class.getName(), registryTestEmbeddedUserGroup.getUserGroupId());

        // then
        assertThat(result).isNotNull();

        // and when
        flushEntityManager(entityManager);
        final RegistryTestEmbeddedUserGroup loaded = findRegistryTestEmbeddedUserGroup(entityManager, registryTestEmbeddedUserGroup.getUserGroupId());

        // then
        assertThat(loaded).isNull();
    }

    @Test
    void shouldDeleteRegistryEntityWithEmbeddedId() {
        // given
        final RegistryTestEntityWithEmbeddedId registryTestEntityWithEmbeddedId = createRegistryTestEntityWithEmbeddedId(entityManager);

        // when
        final RegistryTestEntityWithEmbeddedId result = registryDataService.delete(RegistryTestEntityWithEmbeddedId.class.getName(), registryTestEntityWithEmbeddedId.getId().asMap());

        // then
        assertThat(result).isNotNull();

        // and when
        flushEntityManager(entityManager);
        final RegistryTestEntityWithEmbeddedId loaded = entityManager.find(RegistryTestEntityWithEmbeddedId.class, registryTestEntityWithEmbeddedId.getId());

        // then
        assertThat(loaded).isNull();
    }

    @Test
    void shouldThrowExceptionOnInvalidPrimaryKey() {
        // given
        createRegistryTestEmbeddedUserGroup(entityManager);

        // when
        final Throwable thrown = catchThrowable(() -> registryDataService.delete(RegistryTestEmbeddedUserGroup.class.getName(), new Object()));

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

    @Test
    void shouldThrowExceptionWhenConfigurationHasNotBeenDefined() {
        // given
        final ListRegistryRequest request = createListRegistryRequest("undefined.configuration", "name%");

        // when
        final Throwable thrown = catchThrowable(() -> registryDataService.list(request));

        // then
        assertThat(thrown).isNotNull();
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldDeleteRegistryEntityWithIdClassId() {
        // given
        final RegistryTestEntityWithIdClass registryTestEntityWithIdClass = createRegistryTestEntityWithIdClass(entityManager);

        // when
        final RegistryTestEntityWithIdClass result = registryDataService.delete(RegistryTestEntityWithIdClass.class.getName(), registryTestEntityWithIdClassId(registryTestEntityWithIdClass));

        // then
        assertThat(result).isNotNull();

        // and when
        flushEntityManager(entityManager);
        final RegistryTestEntityWithIdClass loaded = findRegistryTestEntityWithIdClass(entityManager, registryTestEntityWithIdClass);

        // then
        assertThat(loaded).isNull();
    }
}
