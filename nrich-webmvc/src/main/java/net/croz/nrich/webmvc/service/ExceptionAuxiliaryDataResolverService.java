package net.croz.nrich.webmvc.service;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public interface ExceptionAuxiliaryDataResolverService {

    Map<String, Object> resolveRequestExceptionAuxiliaryData(Exception exception, HttpServletRequest request);

}
