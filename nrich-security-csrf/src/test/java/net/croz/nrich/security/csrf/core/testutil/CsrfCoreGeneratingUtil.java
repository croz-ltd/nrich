package net.croz.nrich.security.csrf.core.testutil;

import net.croz.nrich.security.csrf.core.model.CsrfExcludeConfig;

public final class CsrfCoreGeneratingUtil {

    private CsrfCoreGeneratingUtil() {
    }

    public static CsrfExcludeConfig csrfExcludeConfig(final String uri, final String regex) {
        final CsrfExcludeConfig csrfExcludeConfig = new CsrfExcludeConfig();

        csrfExcludeConfig.setUri(uri);
        csrfExcludeConfig.setRegex(regex);

        return csrfExcludeConfig;
    }
}
