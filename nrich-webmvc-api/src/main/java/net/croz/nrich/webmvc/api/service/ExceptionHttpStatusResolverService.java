package net.croz.nrich.webmvc.api.service;

// TODO for now returning Integer instead of Springs HttpStatus to avoid dependency on spring-web, not sure about that decision
public interface ExceptionHttpStatusResolverService {

    Integer resolveHttpStatusForException(Exception exception);

}
