package net.croz.nrich.registry.configuration.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import net.croz.nrich.registry.configuration.model.RegistryCategoryConfiguration;
import net.croz.nrich.registry.test.BaseWebTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class RegistryConfigurationControllerTest extends BaseWebTest {

    @Test
    void shouldFetchRegistryConfiguration() throws Exception {
        // when
        final MockHttpServletResponse response = mockMvc.perform(post("/nrich/registry/configuration/fetch").contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());

        // and when
        final List<RegistryCategoryConfiguration> convertedResponse = objectMapper.readValue(response.getContentAsString(), new TypeReference<List<RegistryCategoryConfiguration>>() {});

        // then
        assertThat(convertedResponse).isNotNull();
        assertThat(convertedResponse).hasSize(3);
    }

}
