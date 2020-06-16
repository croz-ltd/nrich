package net.croz.nrich.webmvc.api.service;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public interface ExceptionAuxiliaryDataResolverService {

    Map<String, Object> resolveRequestExceptionAuxiliaryData(Exception exception, HttpServletRequest request);

}
