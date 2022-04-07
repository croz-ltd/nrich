package net.croz.nrich.webmvc.advice;

import net.croz.nrich.webmvc.test.BaseControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.bind.WebDataBinder;

import java.util.Map;

import static net.croz.nrich.webmvc.advice.testutil.MapGeneratingUtil.createMap;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ControllerEditorRegistrationAdviceTest extends BaseControllerTest {

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
        String requestUrl = fullUrl("empty-strings-to-null");
        Map<String, String> request = createMap("nonEmptyString", "value", "emptyString", "");

        // when
        ResultActions result = performFormPostRequest(requestUrl, request);

        // then
        result.andExpect(status().isOk())
            .andExpect(content().string("value=null"));
    }

    @Test
    void shouldNotBindTransientFields() throws Exception {
        // given
        String requestUrl = fullUrl("transient-properties-serialization");
        String value = "value";
        Map<String, String> request = createMap("transientProperty", "transient", "property", "nonTransient");

        // when
        ResultActions result = performFormPostRequest(requestUrl, request);

        // then
        result.andExpect(status().isOk())
            .andExpect(content().string("value=nonTransient transientValue=null"));
    }

    private String fullUrl(String path) {
        return String.format("/controller-editor-registration-advice-test-controller/%s", path);
    }
}
