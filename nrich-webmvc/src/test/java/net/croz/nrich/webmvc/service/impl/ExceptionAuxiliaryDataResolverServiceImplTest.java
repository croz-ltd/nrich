package net.croz.nrich.webmvc.service.impl;

import net.croz.nrich.webmvc.WebmvcTestConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitWebConfig(WebmvcTestConfiguration.class)
public class ExceptionAuxiliaryDataResolverServiceImplTest {

    @Autowired
    private ExceptionAuxiliaryDataResolverServiceImpl exceptionAuxiliaryDataResolverService;

    @Test
    void shouldResolveExceptionAuxiliaryData() {
        // given
        final RuntimeException exception = new RuntimeException("Exception");
        final MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest("POST", "uri");

        // when
        final Map<String, Object> additionalExceptionData = exceptionAuxiliaryDataResolverService.resolveRequestExceptionAuxiliaryData(exception, mockHttpServletRequest);

        // then
        assertThat(additionalExceptionData).isNotNull();
        assertThat(additionalExceptionData.get("uuid")).isNotNull();
        assertThat(additionalExceptionData.get("occurrenceTime")).isNotNull();
        assertThat(additionalExceptionData.get("requestUri")).isEqualTo("uri");
        assertThat(additionalExceptionData.get("requestMethod")).isEqualTo("POST");
    }
}
