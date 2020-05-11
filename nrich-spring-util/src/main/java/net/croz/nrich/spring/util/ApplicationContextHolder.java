package net.croz.nrich.spring.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public final class ApplicationContextHolder implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    private ApplicationContextHolder() {
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) {
        ApplicationContextHolder.applicationContext = applicationContext;
    }
}
