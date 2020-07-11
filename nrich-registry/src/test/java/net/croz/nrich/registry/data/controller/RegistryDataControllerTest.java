package net.croz.nrich.registry.data.controller;

import lombok.Getter;
import lombok.Setter;
import net.croz.nrich.registry.data.request.CreateRegistryRequest;
import net.croz.nrich.registry.data.request.DeleteRegistryRequest;
import net.croz.nrich.registry.data.request.ListBulkRegistryRequest;
import net.croz.nrich.registry.data.request.ListRegistryRequest;
import net.croz.nrich.registry.data.request.UpdateRegistryRequest;
import net.croz.nrich.registry.data.stub.RegistryTestEmbeddedUserGroup;
import net.croz.nrich.registry.data.stub.RegistryTestEntity;
import net.croz.nrich.registry.test.BaseWebTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Map;

import static net.croz.nrich.registry.data.testutil.RegistryDataGeneratingUtil.createBulkListRegistryRequest;
import static net.croz.nrich.registry.data.testutil.RegistryDataGeneratingUtil.createDeleteEmbeddedUserGroupRequest;
import static net.croz.nrich.registry.data.testutil.RegistryDataGeneratingUtil.createDeleteRegistryRequest;
import static net.croz.nrich.registry.data.testutil.RegistryDataGeneratingUtil.createListRegistryRequest;
import static net.croz.nrich.registry.data.testutil.RegistryDataGeneratingUtil.createRegistryRequest;
import static net.croz.nrich.registry.data.testutil.RegistryDataGeneratingUtil.createRegistryTestEmbeddedUserGroup;
import static net.croz.nrich.registry.data.testutil.RegistryDataGeneratingUtil.createRegistryTestEntity;
import static net.croz.nrich.registry.data.testutil.RegistryDataGeneratingUtil.createRegistryTestEntityList;
import static net.croz.nrich.registry.data.testutil.RegistryDataGeneratingUtil.createRegistryTestEntityWithParent;
import static net.croz.nrich.registry.data.testutil.RegistryDataGeneratingUtil.updateRegistryRequest;
import static net.croz.nrich.registry.testutil.PersistenceTestUtil.executeInTransaction;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class RegistryDataControllerTest extends BaseWebTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private PlatformTransactionManager platformTransactionManager;

    @Test
    void shouldBulkListRegistry() throws Exception {
        // given
        executeInTransaction(platformTransactionManager, () -> createRegistryTestEntityList(entityManager));

        final ListBulkRegistryRequest request = createBulkListRegistryRequest(RegistryTestEntity.class.getName(), "name%");

        // when
        final MockHttpServletResponse response = mockMvc.perform(post("/nrich/registry/data/list-bulk").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request))).andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());

        // and when
        final Map<?, ?> convertedResponse = objectMapper.readValue(response.getContentAsString(), Map.class);

        // then
        assertThat(convertedResponse).isNotNull();
        assertThat(convertedResponse.get(RegistryTestEntity.class.getName())).isNotNull();
    }

    @Test
    void shouldListRegistry() throws Exception {
        // given
        executeInTransaction(platformTransactionManager, () -> createRegistryTestEntityList(entityManager));

        final ListRegistryRequest request = createListRegistryRequest(RegistryTestEntity.class.getName(), "name%");

        // when
        final MockHttpServletResponse response = mockMvc.perform(post("/nrich/registry/data/list").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request))).andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());

        // and when
        final PageHolder convertedResponse = objectMapper.readValue(response.getContentAsString(), PageHolder.class);

        // then
        assertThat(convertedResponse).isNotNull();
        assertThat(convertedResponse.getContent()).hasSize(5);
    }

    @Test
    void shouldCreateRegistryEntity() throws Exception {
        // given
        final String entityName = "name for creating";
        final CreateRegistryRequest request = createRegistryRequest(objectMapper, RegistryTestEntity.class.getName(), entityName);

        // when
        final MockHttpServletResponse response = mockMvc.perform(post("/nrich/registry/data/create").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request))).andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());

        // and when
        final RegistryTestEntity convertedResponse = objectMapper.readValue(response.getContentAsString(), RegistryTestEntity.class);

        // then
        assertThat(convertedResponse).isNotNull();
        assertThat(convertedResponse.getName()).isEqualTo(entityName);
    }

    @Test
    void shouldReturnErrorWhenCreateInputDataIsNotValid() throws Exception {
        // given
        final CreateRegistryRequest request = createRegistryRequest(objectMapper, RegistryTestEntity.class.getName(), null);

        // when
        final MockHttpServletResponse response = mockMvc.perform(post("/nrich/registry/data/create").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request))).andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void shouldUpdateRegistryEntity() throws Exception {
        // given
        final RegistryTestEntity registryTestEntity = executeInTransaction(platformTransactionManager, () -> createRegistryTestEntity(entityManager));
        final String entityName = "name for creating update";
        final UpdateRegistryRequest request = updateRegistryRequest(objectMapper, RegistryTestEntity.class.getName(), registryTestEntity.getId(), entityName);

        // when
        final MockHttpServletResponse response = mockMvc.perform(post("/nrich/registry/data/update").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request))).andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());

        // and when
        final RegistryTestEntity convertedResponse = objectMapper.readValue(response.getContentAsString(), RegistryTestEntity.class);

        // then
        assertThat(convertedResponse).isNotNull();
        assertThat(convertedResponse.getName()).isEqualTo(entityName);
    }

    @Test
    void shouldReturnErrorWhenUpdatingWithInvalidData() throws Exception {
        // given
        final RegistryTestEntity registryTestEntity = executeInTransaction(platformTransactionManager, () -> createRegistryTestEntity(entityManager));
        final UpdateRegistryRequest request = updateRegistryRequest(objectMapper, RegistryTestEntity.class.getName(), registryTestEntity.getId(), null);

        // when
        final MockHttpServletResponse response = mockMvc.perform(post("/nrich/registry/data/update").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request))).andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void shouldNotFailUpdatingRegistryEntityWithAssociation() throws Exception {
        // given
        final RegistryTestEntity registryTestEntity = executeInTransaction(platformTransactionManager, () -> createRegistryTestEntityWithParent(entityManager));
        final String entityName = "name for update";
        final UpdateRegistryRequest request = updateRegistryRequest(objectMapper, RegistryTestEntity.class.getName(), registryTestEntity.getId(), entityName);

        // when
        final MockHttpServletResponse response = mockMvc.perform(post("/nrich/registry/data/update").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request))).andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void shouldDeleteRegistryEntity() throws Exception {
        // given
        final RegistryTestEntity registryTestEntity = executeInTransaction(platformTransactionManager, () -> createRegistryTestEntity(entityManager));

        final DeleteRegistryRequest request = createDeleteRegistryRequest(RegistryTestEntity.class.getName(), registryTestEntity.getId());

        // when
        final MockHttpServletResponse response = mockMvc.perform(post("/nrich/registry/data/delete").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request))).andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());

        // and when
        final RegistryTestEntity convertedResponse = objectMapper.readValue(response.getContentAsString(), RegistryTestEntity.class);

        // then
        assertThat(convertedResponse.getId()).isEqualTo(registryTestEntity.getId());
    }

    @Test
    void shouldDeleteRegistryEntityWithEmbeddedId() throws Exception {
        // given
        final RegistryTestEmbeddedUserGroup registryTestEmbeddedUserGroup = executeInTransaction(platformTransactionManager, () -> createRegistryTestEmbeddedUserGroup(entityManager));

        final DeleteRegistryRequest request = createDeleteEmbeddedUserGroupRequest(registryTestEmbeddedUserGroup.getUserGroupId());

        // when
        final MockHttpServletResponse response = mockMvc.perform(post("/nrich/registry/data/delete").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request))).andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());

        // and when
        final RegistryTestEmbeddedUserGroup convertedResponse = objectMapper.readValue(response.getContentAsString(), RegistryTestEmbeddedUserGroup.class);

        // then
        assertThat(convertedResponse.getUserGroupId()).isEqualTo(registryTestEmbeddedUserGroup.getUserGroupId());
    }

    @AfterEach
    void cleanup() {
        executeInTransaction(platformTransactionManager, () -> entityManager.createQuery("delete from " + RegistryTestEntity.class.getName()).executeUpdate());
    }

    @Setter
    @Getter
    static class PageHolder {

        private List<RegistryTestEntity> content;

    }
}
