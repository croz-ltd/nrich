package net.croz.nrich.registry.data.controller;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.croz.nrich.registry.data.request.CreateRegistryRequest;
import net.croz.nrich.registry.data.request.DeleteRegistryRequest;
import net.croz.nrich.registry.data.request.ListRegistryRequest;
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
import javax.validation.ConstraintViolationException;
import java.util.List;

import static net.croz.nrich.registry.data.testutil.RegistryDataGeneratingUtil.createDeleteRegistryRequest;
import static net.croz.nrich.registry.data.testutil.RegistryDataGeneratingUtil.createListRegistryRequest;
import static net.croz.nrich.registry.data.testutil.RegistryDataGeneratingUtil.createRegistryRequest;
import static net.croz.nrich.registry.data.testutil.RegistryDataGeneratingUtil.createRegistryTestEntity;
import static net.croz.nrich.registry.data.testutil.RegistryDataGeneratingUtil.createRegistryTestEntityList;
import static net.croz.nrich.registry.testutil.PersistenceTestUtil.executeInTransaction;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class RegistryDataControllerTest extends BaseWebTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private PlatformTransactionManager platformTransactionManager;

    @SneakyThrows
    @Test
    void shouldListRegistry() {
        // given
        executeInTransaction(platformTransactionManager, () -> createRegistryTestEntityList(entityManager));

        final ListRegistryRequest request = createListRegistryRequest(RegistryTestEntity.class.getName(), "name%");

        // when
        final MockHttpServletResponse response = mockMvc.perform(post("/nrichRegistryData/list").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request))).andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());

        // and when
        final PageHolder convertedResponse = objectMapper.readValue(response.getContentAsString(), PageHolder.class);

        // then
        assertThat(convertedResponse).isNotNull();
        assertThat(convertedResponse.getContent()).hasSize(5);
    }

    @SneakyThrows
    @Test
    void shouldCreateRegistryEntity() {
        // given
        final String entityName = "name for creating";
        final CreateRegistryRequest request = createRegistryRequest(objectMapper, RegistryTestEntity.class.getName(), entityName);

        // when
        final MockHttpServletResponse response = mockMvc.perform(post("/nrichRegistryData/create").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request))).andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());

        // and when
        final RegistryTestEntity convertedResponse = objectMapper.readValue(response.getContentAsString(), RegistryTestEntity.class);

        // then
        assertThat(convertedResponse).isNotNull();
        assertThat(convertedResponse.getName()).isEqualTo(entityName);
    }

    @SneakyThrows
    @Test
    void shouldReturnErrorWhenCreateInputDataIsNotValid() {
        // given
        final CreateRegistryRequest request = createRegistryRequest(objectMapper, RegistryTestEntity.class.getName(), null);

        // when
        final Throwable thrown = catchThrowable(() ->mockMvc.perform(post("/nrichRegistryData/create").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request))).andReturn().getResponse());

        // then
        assertThat(thrown.getCause()).isInstanceOf(ConstraintViolationException.class);
    }

    @SneakyThrows
    @Test
    void shouldDeleteRegistryEntity() {
        // given
        final RegistryTestEntity registryTestEntity = executeInTransaction(platformTransactionManager, () -> createRegistryTestEntity(entityManager));

        final DeleteRegistryRequest request = createDeleteRegistryRequest(RegistryTestEntity.class.getName(), registryTestEntity.getId());

        // when
        final MockHttpServletResponse response = mockMvc.perform(post("/nrichRegistryData/delete").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request))).andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());

        // and when
        final boolean convertedResponse = objectMapper.readValue(response.getContentAsString(), Boolean.class);

        // then
        assertThat(convertedResponse).isTrue();
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
