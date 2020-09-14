package net.croz.nrich.webmvc.service;

import net.croz.nrich.webmvc.WebmvcTestConfiguration;
import net.croz.nrich.webmvc.service.stub.TransientPropertyResolverServiceImplTestRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitWebConfig(WebmvcTestConfiguration.class)
class DefaultTransientPropertyResolverServiceTest {

    @Autowired
    private DefaultTransientPropertyResolverService transientPropertyResolverService;

    @Test
    void shouldResolveTransientPropertyList() {
        // given
        final Class<?> type = TransientPropertyResolverServiceImplTestRequest.class;

        // when
        final List<String> resultList = transientPropertyResolverService.resolveTransientPropertyList(type);

        // then
        assertThat(resultList).containsExactlyInAnyOrder("value", "anotherValue");

        // and when
        final List<String> cachedResultList = transientPropertyResolverService.resolveTransientPropertyList(type);

        // then
        assertThat(cachedResultList).containsExactlyInAnyOrder("value", "anotherValue");

    }
}
