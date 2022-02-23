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
@RequestMapping("notificationErrorHandlingRestControllerAdviceTest")
@RestController
public class NotificationErrorHandlingRestControllerAdviceTestController {

    private final NotificationErrorHandlingRestControllerAdviceTestService notificationErrorHandlingRestControllerAdviceTestService;

    @PostMapping("exceptionResolving")
    public void exceptionResolving() {
        throw new NotificationErrorHandlingRestControllerAdviceTestException();
    }

    @PostMapping("exceptionResolvingWithArguments")
    public void exceptionResolvingWithArguments() {
        throw new NotificationErrorHandlingRestControllerAdviceTestExceptionWithArguments("message", 1);
    }

    @PostMapping("validationFailedResolving")
    public String validationFailedResolving(@Valid @RequestBody NotificationErrorHandlingRestControllerAdviceTestRequest request) {
        return request.getName();
    }

    @PostMapping("bindValidationFailedResolving")
    public String bindValidationFailedResolving(@Valid NotificationErrorHandlingRestControllerAdviceTestRequest request) {
        return request.getName();
    }

    @PostMapping("validationFailedBindExceptionResolving")
    public String validationFailedBindExceptionResolving(@Valid NotificationErrorHandlingRestControllerAdviceTestRequest request) {
        return request.getName();
    }

    @PostMapping("unwrappedExceptionResolving")
    public void unwrappedExceptionResolving() throws Exception {
        throw new ExecutionException(new NotificationErrorHandlingRestControllerAdviceTestException());
    }

    @PostMapping("unwrappedExceptionValidationFailedResolving")
    public void unwrappedExceptionValidationFailedResolving() throws Exception {
        MethodParameter methodParameter = new MethodParameter(this.getClass().getMethods()[1], -1);
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(methodParameter, mock(BindingResult.class));

        throw new ExecutionException(exception);
    }

    @PostMapping("unwrappedExceptionBindExceptionResolving")
    public void unwrappedExceptionBindExceptionResolving() throws Exception {
        BindException exception = new BindException(mock(BindingResult.class));

        throw new ExecutionException(exception);
    }

    @PostMapping("constraintViolationExceptionResolving")
    public String constraintViolationExceptionResolving() {
        return notificationErrorHandlingRestControllerAdviceTestService.validationFailedResolving(new NotificationErrorHandlingRestControllerAdviceTestRequest());
    }
}
