package net.croz.nrich.registry.configuration.controller;

import net.croz.nrich.registry.test.BaseControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestPropertySource(properties = "nrich.registry.configuration.endpoint-path=api/registry/configuration")
class RegistryConfigurationControllerEndpointTest extends BaseControllerTest {

    @Test
    void shouldFetchRegistryConfiguration() throws Exception {
        // given
        String requestUrl = "/api/registry/configuration/fetch";

        // when
        ResultActions result = performPostRequest(requestUrl);

        // then
        result.andExpect(status().isOk());
    }
}
