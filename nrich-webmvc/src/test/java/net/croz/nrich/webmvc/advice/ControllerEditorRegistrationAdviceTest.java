package net.croz.nrich.webmvc.advice;

import net.croz.nrich.webmvc.test.BaseWebTest;
import org.junit.jupiter.api.Test;
import org.springframework.web.bind.WebDataBinder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

class ControllerEditorRegistrationAdviceTest extends BaseWebTest {

    @Test
    void shouldNotRegisterBindersWhenDisabled() {
        // given
        WebDataBinder webDataBinder = mock(WebDataBinder.class);
        ControllerEditorRegistrationAdvice controllerEditorRegistrationAdvice = new ControllerEditorRegistrationAdvice(false, false, null);

        // when
        controllerEditorRegistrationAdvice.initBinder(webDataBinder);

        // then
        verifyNoInteractions(webDataBinder);
    }

    @Test
    void shouldConvertEmptyStringsToNull() throws Exception {
        // given
        String emptyString = " ";

        // when
        String response = mockMvc.perform(post("/controllerEditorRegistrationAdviceTestController/convertEmptyStringToNull").param("param", emptyString)).andReturn().getResponse().getContentAsString();

        // then
        assertThat(response).isEqualTo("value=null");
    }

    @Test
    void shouldNotBindTransientFields() throws Exception {
        // when
        String response = mockMvc.perform(post("/controllerEditorRegistrationAdviceTestController/ignoreTransientProperty").param("transientProperty", "transient").param("property", "nonTransient")).andReturn().getResponse().getContentAsString();

        // then
        assertThat(response).isEqualTo("value=nonTransient transientValue=null");
    }
}
