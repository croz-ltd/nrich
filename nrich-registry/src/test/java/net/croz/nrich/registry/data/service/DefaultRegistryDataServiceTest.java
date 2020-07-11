package net.croz.nrich.registry.data.service;

import net.croz.nrich.registry.RegistryTestConfiguration;
import net.croz.nrich.registry.data.request.CreateRegistryServiceRequest;
import net.croz.nrich.registry.data.request.DeleteRegistryRequest;
import net.croz.nrich.registry.data.request.ListBulkRegistryRequest;
import net.croz.nrich.registry.data.request.ListRegistryRequest;
import net.croz.nrich.registry.data.request.UpdateRegistryServiceRequest;
import net.croz.nrich.registry.data.stub.RegistryTestEmbeddedUserGroup;
import net.croz.nrich.registry.data.stub.RegistryTestEmbeddedUserGroupId;
import net.croz.nrich.registry.data.stub.RegistryTestEntity;
import net.croz.nrich.registry.data.stub.RegistryTestEntityWithDifferentIdName;
import net.croz.nrich.registry.data.stub.RegistryTestEntityWithEmbeddedId;
import net.croz.nrich.registry.data.stub.RegistryTestEntityWithIdClass;
import net.croz.nrich.registry.data.stub.RegistryTestEntityWithOverriddenSearchConfiguration;
import net.croz.nrich.registry.data.stub.RegistryTestEntityWithoutAssociation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Map;

import static net.croz.nrich.registry.data.testutil.RegistryDataGeneratingUtil.createBulkListRegistryRequest;
import static net.croz.nrich.registry.data.testutil.RegistryDataGeneratingUtil.createDeleteEmbeddedUserGroupRequest;
import static net.croz.nrich.registry.data.testutil.RegistryDataGeneratingUtil.createDeleteRegistryRequest;
import static net.croz.nrich.registry.data.testutil.RegistryDataGeneratingUtil.createDeleteRegistryTestEntityWithIdClassRequest;
import static net.croz.nrich.registry.data.testutil.RegistryDataGeneratingUtil.createListRegistryRequest;
import static net.croz.nrich.registry.data.testutil.RegistryDataGeneratingUtil.createRegistryServiceRequest;
import static net.croz.nrich.registry.data.testutil.RegistryDataGeneratingUtil.createRegistryTestEmbeddedUserGroup;
import static net.croz.nrich.registry.data.testutil.RegistryDataGeneratingUtil.createRegistryTestEmbeddedUserGroupId;
import static net.croz.nrich.registry.data.testutil.RegistryDataGeneratingUtil.createRegistryTestEntity;
import static net.croz.nrich.registry.data.testutil.RegistryDataGeneratingUtil.createRegistryTestEntityList;
import static net.croz.nrich.registry.data.testutil.RegistryDataGeneratingUtil.createRegistryTestEntityWithDifferentIdNameList;
import static net.croz.nrich.registry.data.testutil.RegistryDataGeneratingUtil.createRegistryTestEntityWithEmbeddedId;
import static net.croz.nrich.registry.data.testutil.RegistryDataGeneratingUtil.createRegistryTestEntityWithIdClass;
import static net.croz.nrich.registry.data.testutil.RegistryDataGeneratingUtil.createRegistryTestEntityWithOverriddenSearchConfigurationList;
import static net.croz.nrich.registry.data.testutil.RegistryDataGeneratingUtil.createRegistryTestEntityWithoutAssociation;
import static net.croz.nrich.registry.data.testutil.RegistryDataGeneratingUtil.createUpdateEmbeddedUserGroupRequest;
import static net.croz.nrich.registry.data.testutil.RegistryDataGeneratingUtil.updateRegistryServiceRequest;
import static net.croz.nrich.registry.data.testutil.RegistryDataResolvingUtil.findRegistryTestEmbeddedUserGroup;
import static net.croz.nrich.registry.data.testutil.RegistryDataResolvingUtil.findRegistryTestEntityWithIdClass;
import static net.croz.nrich.registry.testutil.PersistenceTestUtil.flushEntityManager;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@Transactional
@SpringJUnitWebConfig(RegistryTestConfiguration.class)
public class DefaultRegistryDataServiceTest {

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
        final Map<String, Page<RegistryTestEntity>> result = registryDataService.listBulk(request);

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
    void shouldUpdateRegistryEntityWithoutAssociations() {
        // given
        final RegistryTestEntityWithoutAssociation registryTestEntity = createRegistryTestEntityWithoutAssociation(entityManager);

        final UpdateRegistryServiceRequest request = updateRegistryServiceRequest(RegistryTestEntityWithoutAssociation.class.getName(), registryTestEntity.getId());

        // when
        final RegistryTestEntityWithoutAssociation updatedEntity = registryDataService.update(request);

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

        final UpdateRegistryServiceRequest request = createUpdateEmbeddedUserGroupRequest(registryTestEmbeddedUserGroup.getUserGroupId(), registryUpdateGroupId, joinedPropertyUpdateValue);

        // when
        final RegistryTestEmbeddedUserGroup result = registryDataService.update(request);

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

        final DeleteRegistryRequest request = createDeleteRegistryRequest(RegistryTestEntity.class.getName(), registryTestEntity.getId());

        // when
        final RegistryTestEntity result = registryDataService.delete(request);

        // then
        assertThat(result.getId()).isEqualTo(registryTestEntity.getId());
    }

    @Test
    void shouldDeleteRegistryEntityWithEmbeddedObjectId() {
        // given
        final RegistryTestEmbeddedUserGroup registryTestEmbeddedUserGroup = createRegistryTestEmbeddedUserGroup(entityManager);

        final DeleteRegistryRequest request = createDeleteEmbeddedUserGroupRequest(registryTestEmbeddedUserGroup.getUserGroupId());

        // when
        final RegistryTestEmbeddedUserGroup result = registryDataService.delete(request);

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

        final DeleteRegistryRequest request = createDeleteRegistryRequest(RegistryTestEntityWithEmbeddedId.class.getName(), registryTestEntityWithEmbeddedId.getId().asMap());

        // when
        final RegistryTestEntityWithEmbeddedId result = registryDataService.delete(request);

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

        final DeleteRegistryRequest request = createDeleteRegistryTestEntityWithIdClassRequest(registryTestEntityWithIdClass);

        // when
        final RegistryTestEntityWithIdClass result = registryDataService.delete(request);

        // then
        assertThat(result).isNotNull();

        // and when
        flushEntityManager(entityManager);
        final RegistryTestEntityWithIdClass loaded = findRegistryTestEntityWithIdClass(entityManager, registryTestEntityWithIdClass);

        // then
        assertThat(loaded).isNull();
    }
}
