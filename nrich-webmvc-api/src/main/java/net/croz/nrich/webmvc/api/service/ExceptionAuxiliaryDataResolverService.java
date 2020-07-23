package net.croz.nrich.webmvc.api.service;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Resolves auxiliary data for exception (i.e UUID, request uri etc)
 */
public interface ExceptionAuxiliaryDataResolverService {

    /**
     * Returns map containing auxiliary data for exception and request.
     *
     * @param exception for which to resolve auxiliary data
     * @param request current http request
     * @return map of auxiliary data
     */
    Map<String, Object> resolveRequestExceptionAuxiliaryData(Exception exception, HttpServletRequest request);

}
