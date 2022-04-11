package net.croz.nrich.webmvc.advice.stub;

import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.concurrent.ExecutionException;

import static org.mockito.Mockito.mock;

@RequiredArgsConstructor
@RequestMapping("notification-error-handling-test-controller")
@RestController
public class NotificationErrorHandlingRestControllerAdviceTestController {

    private final NotificationErrorHandlingRestControllerAdviceTestService notificationErrorHandlingRestControllerAdviceTestService;

    @PostMapping("exception-resolving")
    public void exceptionResolving() {
        throw new NotificationErrorHandlingRestControllerAdviceTestException();
    }

    @PostMapping("exception-resolving-with-arguments")
    public void exceptionResolvingWithArguments() {
        throw new NotificationErrorHandlingRestControllerAdviceTestExceptionWithArguments("message", 1);
    }

    @PostMapping("validation-failed-resolving")
    public String validationFailedResolving(@Valid @RequestBody NotificationErrorHandlingRestControllerAdviceTestRequest request) {
        return request.getName();
    }

    @PostMapping("bind-validation-failed-resolving")
    public String bindValidationFailedResolving(@Valid NotificationErrorHandlingRestControllerAdviceTestRequest request) {
        return request.getName();
    }

    @PostMapping("unwrapped-exception-resolving")
    public void unwrappedExceptionResolving() throws Exception {
        throw new ExecutionException(new NotificationErrorHandlingRestControllerAdviceTestException());
    }

    @PostMapping("unwrapped-exception-validation-failed-resolving")
    public void unwrappedExceptionValidationFailedResolving() throws Exception {
        MethodParameter methodParameter = new MethodParameter(this.getClass().getMethods()[1], -1);
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(methodParameter, mock(BindingResult.class));

        throw new ExecutionException(exception);
    }

    @PostMapping("unwrapped-exception-bind-exception-resolving")
    public void unwrappedExceptionBindExceptionResolving() throws Exception {
        BindException exception = new BindException(mock(BindingResult.class));

        throw new ExecutionException(exception);
    }

    @PostMapping("constraint-violation-exception-resolving")
    public String constraintViolationExceptionResolving() {
        return notificationErrorHandlingRestControllerAdviceTestService.validationFailedResolving(new NotificationErrorHandlingRestControllerAdviceTestRequest());
    }

    @PostMapping("unwrapped-exception-constraint-violation-exception-resolving")
    public String unwrappedExceptionConstraintViolationExceptionExceptionResolving() throws Exception {
        try {
            return notificationErrorHandlingRestControllerAdviceTestService.validationFailedResolving(new NotificationErrorHandlingRestControllerAdviceTestRequest());
        }
        catch (Exception exception) {
            throw new ExecutionException(exception);
        }
    }

    @PostMapping("unwrapped-exception-status-resolving")
    public void unwrappedExceptionStatusResolving() throws Exception {
        throw new ExecutionException(new NotificationErrorHandlingRestControllerAdviceTestExceptionWithStatus());
    }
}
