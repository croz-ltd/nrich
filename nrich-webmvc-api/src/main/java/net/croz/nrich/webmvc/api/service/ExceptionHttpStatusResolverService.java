package net.croz.nrich.webmvc.api.service;

// TODO for now returning Integer instead of Springs HttpStatus to avoid dependency on spring-web, not sure about that decision
/**
 * Resolve http status for exception. If invalid status value is returned status 500 is used.
 */
public interface ExceptionHttpStatusResolverService {

    /**
     * Returns http status value.
     *
     * @param exception exception to resolve status for
     * @return status value
     */
    Integer resolveHttpStatusForException(Exception exception);

}
