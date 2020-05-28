package net.croz.nrich.webmvc.advice.stub;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
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
    public String validationFailedResolving(@Valid @RequestBody final NotificationErrorHandlingRestControllerAdviceTestRequest request) {
        return request.getName();
    }

    @PostMapping("bindValidationFailedResolving")
    public String bindValidationFailedResolving(@Valid final NotificationErrorHandlingRestControllerAdviceTestRequest request) {
        return request.getName();
    }

    @PostMapping("validationFailedBindExceptionResolving")
    public String validationFailedBindExceptionResolving(@Valid final NotificationErrorHandlingRestControllerAdviceTestRequest request) {
        return request.getName();
    }

    @SneakyThrows
    @PostMapping("unwrappedExceptionResolving")
    public void unwrappedExceptionResolving() {
        throw new ExecutionException(new NotificationErrorHandlingRestControllerAdviceTestException());
    }

    @SneakyThrows
    @PostMapping("unwrappedExceptionValidationFailedResolving")
    public void unwrappedExceptionValidationFailedResolving() {
        final MethodParameter methodParameter = new MethodParameter(this.getClass().getMethods()[1], -1);
        final MethodArgumentNotValidException exception = new MethodArgumentNotValidException(methodParameter, mock(BindingResult.class));

        throw new ExecutionException(exception);
    }

    @SneakyThrows
    @PostMapping("unwrappedExceptionBindExceptionResolving")
    public void unwrappedExceptionBindExceptionResolving() {
        final BindException exception = new BindException(mock(BindingResult.class));

        throw new ExecutionException(exception);
    }

    @PostMapping("constraintViolationExceptionResolving")
    public void constraintViolationExceptionResolving() {
        notificationErrorHandlingRestControllerAdviceTestService.validationFailedResolving(new NotificationErrorHandlingRestControllerAdviceTestRequest());
    }
}
