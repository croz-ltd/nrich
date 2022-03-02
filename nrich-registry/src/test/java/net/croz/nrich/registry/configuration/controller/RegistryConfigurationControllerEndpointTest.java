package net.croz.nrich.registry.configuration.controller;

import net.croz.nrich.registry.test.BaseWebTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@TestPropertySource(locations = "/application-test.yml")
class RegistryConfigurationControllerEndpointTest extends BaseWebTest {

    @Test
    void shouldFetchRegistryConfiguration() throws Exception {
        // when
        MockHttpServletResponse response = mockMvc.perform(post("/domain/nrich/registry/configuration/fetch").contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }
}
