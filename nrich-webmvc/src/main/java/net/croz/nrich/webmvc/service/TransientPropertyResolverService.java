package net.croz.nrich.webmvc.service;

import java.util.List;

public interface TransientPropertyResolverService {

    List<String> resolveTransientPropertyList(Class<?> type);

}
