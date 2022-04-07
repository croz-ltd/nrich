package net.croz.nrich.registry.configuration.controller;

import net.croz.nrich.registry.test.BaseControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RegistryConfigurationControllerTest extends BaseControllerTest {

    @Test
    void shouldFetchRegistryConfiguration() throws Exception {
        // given
        String requestUrl = "/nrich/registry/configuration/fetch";

        // when
        ResultActions result = performPostRequest(requestUrl);

        // then
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(3)));

    }
}
