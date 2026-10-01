/*
 *  Copyright 2020-2023 CROZ d.o.o, the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package net.croz.nrich.problemdetail.handler;

import net.croz.nrich.core.api.exception.ExceptionWithArguments;
import net.croz.nrich.core.api.exception.ExceptionWithMessage;
import net.croz.nrich.core.api.exception.ExceptionWithMessageCode;
import net.croz.nrich.logging.api.service.LoggingService;
import net.croz.nrich.problemdetail.api.contributor.ProblemDetailContributor;
import net.croz.nrich.problemdetail.api.contributor.ProblemDetailContributorContext;
import net.croz.nrich.problemdetail.constant.ProblemDetailConstants;
import net.croz.nrich.problemdetail.contributor.DefaultProblemDetailContributorContext;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import jakarta.validation.ConstraintViolationException;
import java.net.URI;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestControllerAdvice
public class NrichProblemDetailExceptionHandler extends ResponseEntityExceptionHandler {

    private final MessageSource messageSource;

    private final LoggingService loggingService;

    private final List<ProblemDetailContributor> contributors;

    private final List<String> exceptionToUnwrapList;

    public NrichProblemDetailExceptionHandler(MessageSource messageSource, LoggingService loggingService, List<ProblemDetailContributor> contributors, List<String> exceptionToUnwrapList) {
        this.messageSource = messageSource;
        this.loggingService = loggingService;
        this.contributors = contributors;
        this.exceptionToUnwrapList = exceptionToUnwrapList;
        setMessageSource(messageSource);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGenericException(Exception exception, WebRequest request) throws Exception {
        Exception unwrapped = unwrap(exception);

        ResponseEntity<Object> reRouted = reRoute(unwrapped, request);
        if (reRouted != null) {
            return reRouted;
        }

        return handleExceptionInternal(unwrapped, ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception exception, Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        HttpStatusCode status = resolveStatus(exception, statusCode);
        String correlationId = UUID.randomUUID().toString();

        logException(exception, correlationId, request);

        ResponseEntity<Object> response = super.handleExceptionInternal(exception, body, headers, status, request);

        if (response != null && response.getBody() instanceof ProblemDetail problemDetail) {
            problemDetail.setStatus(status.value());
            problemDetail.setTitle(resolveTitle(problemDetail, exception));
            problemDetail.setDetail(resolveDetail(exception));

            URI type = resolveType(exception);
            if (type != null) {
                problemDetail.setType(type);
            }

            ProblemDetailContributorContext context = new DefaultProblemDetailContributorContext(exception, request, status, LocaleContextHolder.getLocale(), correlationId, Instant.now());

            contributors.forEach(contributor -> contributor.contribute(problemDetail, context));
        }

        return response;
    }

    protected void logException(Exception exception, String correlationId, WebRequest request) {
        Map<String, Object> auxiliaryData = new LinkedHashMap<>();

        auxiliaryData.put(ProblemDetailConstants.LOG_ERROR_ID_KEY, correlationId);
        auxiliaryData.put(ProblemDetailConstants.LOG_PATH_KEY, request.getDescription(false));
        if (request instanceof ServletWebRequest servletWebRequest) {
            auxiliaryData.put(ProblemDetailConstants.LOG_METHOD_KEY, servletWebRequest.getRequest().getMethod());
        }

        loggingService.logInternalException(exception, auxiliaryData);
    }

    protected ResponseEntity<Object> reRoute(Exception unwrapped, WebRequest request) throws Exception {
        try {
            return super.handleException(unwrapped, request);
        }
        catch (Exception exception) {
            if (exception == unwrapped) {
                // super rethrows the same exception when it does not handle the type; caller falls through to the generic path
                return null;
            }

            throw exception;
        }
    }

    protected Exception unwrap(Exception exception) {
        if (exceptionToUnwrapList != null && exceptionToUnwrapList.contains(exception.getClass().getName()) && exception.getCause() instanceof Exception cause) {
            return cause;
        }

        return exception;
    }

    protected HttpStatusCode resolveStatus(Exception exception, HttpStatusCode current) {
        String configured = messageSource.getMessage(String.format(ProblemDetailConstants.STATUS_CODE_FORMAT, exception.getClass().getName()), null, null, LocaleContextHolder.getLocale());

        if (configured != null) {
            try {
                return HttpStatus.valueOf(Integer.parseInt(configured));
            }
            catch (Exception ignored) {
                // ignored
            }
        }

        ResponseStatus annotation = AnnotatedElementUtils.findMergedAnnotation(exception.getClass(), ResponseStatus.class);
        if (annotation != null) {
            return annotation.code();
        }

        if (isValidationException(exception)) {
            return HttpStatus.BAD_REQUEST;
        }

        return current;
    }

    protected String detailCode(Exception exception) {
        if (exception instanceof ExceptionWithMessageCode exceptionWithMessageCode) {
            return exceptionWithMessageCode.getMessageCode();
        }

        return String.format(ProblemDetailConstants.SPRING_DETAIL_CODE_FORMAT, exception.getClass().getName());
    }

    protected String resolveDetail(Exception exception) {
        Object[] arguments = exceptionArguments(exception);

        if (exception instanceof ExceptionWithMessage) {
            return resolveMessage(new String[] { detailCode(exception) }, arguments, exception.getMessage());
        }

        boolean validationException = isValidationException(exception);
        String defaultDetailCode = validationException ? ProblemDetailConstants.VALIDATION_DETAIL_CODE : ProblemDetailConstants.DEFAULT_DETAIL_CODE;
        String defaultDetail = validationException ? ProblemDetailConstants.DEFAULT_VALIDATION_DETAIL : ProblemDetailConstants.DEFAULT_ERROR_DETAIL;

        return resolveMessage(new String[] { detailCode(exception), defaultDetailCode }, arguments, defaultDetail);
    }

    protected String resolveTitle(ProblemDetail problemDetail, Exception exception) {
        String defaultTitleCode = isValidationException(exception) ? ProblemDetailConstants.VALIDATION_TITLE_CODE : ProblemDetailConstants.DEFAULT_TITLE_CODE;
        String[] codes = { String.format(ProblemDetailConstants.TITLE_CODE_FORMAT, exception.getClass().getName()), defaultTitleCode };

        return resolveMessage(codes, null, problemDetail.getTitle());
    }

    protected URI resolveType(Exception exception) {
        String resolved = messageSource.getMessage(String.format(ProblemDetailConstants.TYPE_CODE_FORMAT, exception.getClass().getName()), null, null, LocaleContextHolder.getLocale());

        return resolved == null ? null : URI.create(resolved);
    }

    protected Object[] exceptionArguments(Exception exception) {
        if (exception instanceof ExceptionWithArguments exceptionWithArguments) {
            return exceptionWithArguments.getArgumentList();
        }

        if (exception instanceof ErrorResponse errorResponse) {
            return errorResponse.getDetailMessageArguments(messageSource, LocaleContextHolder.getLocale());
        }

        return null;
    }

    private String resolveMessage(String[] codes, Object[] arguments, String defaultMessage) {
        return messageSource.getMessage(new DefaultMessageSourceResolvable(codes, arguments, defaultMessage), LocaleContextHolder.getLocale());
    }

    private boolean isValidationException(Exception exception) {
        return exception instanceof MethodArgumentNotValidException || exception instanceof ConstraintViolationException || exception instanceof HandlerMethodValidationException;
    }
}
