package net.croz.nrich.webmvc.advice;

import lombok.SneakyThrows;
import net.croz.nrich.webmvc.test.BaseWebTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class ControllerEditorRegistrationAdviceTest extends BaseWebTest {

    @SneakyThrows
    @Test
    void shouldConvertEmptyStringsToNull() {
        // given
        final String emptyString = " ";

        // when
        final String response = mockMvc.perform(post("/controllerEditorRegistrationAdviceTestController/convertEmptyStringToNull").param("param", emptyString)).andReturn().getResponse().getContentAsString();

        // then
        assertThat(response).isEqualTo("value=null");
    }

    @SneakyThrows
    @Test
    void shouldNotBindTransientFields() {
        // when
        final String response = mockMvc.perform(post("/controllerEditorRegistrationAdviceTestController/ignoreTransientProperty").param("transientProperty", "transient").param("property", "nonTransient")).andReturn().getResponse().getContentAsString();

        // then
        assertThat(response).isEqualTo("value=nonTransient transientValue=null");
    }

}
