package net.croz.nrich.webmvc.service;

import lombok.RequiredArgsConstructor;
import net.croz.nrich.webmvc.api.service.ExceptionHttpStatusResolverService;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.DefaultMessageSourceResolvable;

@RequiredArgsConstructor
public class MessageSourceExceptionHttpStatusResolverService implements ExceptionHttpStatusResolverService {

    private static final String PREFIX_FORMAT = "%s.%s";

    private static final String EXCEPTION_HTTP_STATUS_SUFFIX = "httpStatus";

    private final MessageSource messageSource;

    @Override
    public Integer resolveHttpStatusForException(final Exception exception) {
        final String statusMessageCode = String.format(PREFIX_FORMAT, exception.getClass().getName(), EXCEPTION_HTTP_STATUS_SUFFIX);

        final DefaultMessageSourceResolvable defaultMessageSourceResolvable = new DefaultMessageSourceResolvable(statusMessageCode);

        Integer status = null;
        try {
            status = Integer.valueOf(messageSource.getMessage(defaultMessageSourceResolvable, LocaleContextHolder.getLocale()));
        }
        catch (final Exception ignored) {
            // ignored
        }

        return status;
    }
}
