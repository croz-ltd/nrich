package net.croz.nrich.security.csrf.core.util;

import net.croz.nrich.security.csrf.core.model.CsrfExcludeConfig;
import org.springframework.util.CollectionUtils;

import java.util.List;

public final class CsrfUriUtil {

    private CsrfUriUtil() {
    }

    public static boolean excludeUri(List<CsrfExcludeConfig> csrfExcludeConfigList, String uri) {
        if (CollectionUtils.isEmpty(csrfExcludeConfigList)) {
            return false;
        }

        return csrfExcludeConfigList.stream()
            .anyMatch(csrfExcludeConfig ->
                csrfExcludeConfig.getUri() != null && csrfExcludeConfig.getUri().endsWith(uri) || csrfExcludeConfig.getRegex() != null && uri.matches(csrfExcludeConfig.getRegex())
            );
    }
}
