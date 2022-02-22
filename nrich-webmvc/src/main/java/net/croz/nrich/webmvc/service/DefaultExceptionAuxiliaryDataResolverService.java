package net.croz.nrich.webmvc.service;

import net.croz.nrich.webmvc.api.service.ExceptionAuxiliaryDataResolverService;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class DefaultExceptionAuxiliaryDataResolverService implements ExceptionAuxiliaryDataResolverService {

    @Override
    public Map<String, Object> resolveRequestExceptionAuxiliaryData(Exception exception, HttpServletRequest request) {
        Map<String, Object> resultMap = new LinkedHashMap<>();

        resultMap.put("uuid", UUID.randomUUID().toString());
        resultMap.put("occurrenceTime", Instant.now());
        resultMap.put("requestUri", request.getRequestURI());
        resultMap.put("requestMethod", request.getMethod());

        return resultMap;
    }
}
