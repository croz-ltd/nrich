package net.croz.nrich.webmvc.advice.stub;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

@Validated
@Service
public class NotificationErrorHandlingRestControllerAdviceTestService {

    public String validationFailedResolving(@Valid NotificationErrorHandlingRestControllerAdviceTestRequest request) {
        return request.getName();
    }
}
