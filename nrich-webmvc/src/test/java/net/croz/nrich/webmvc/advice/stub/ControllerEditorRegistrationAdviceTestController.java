package net.croz.nrich.webmvc.advice.stub;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("controllerEditorRegistrationAdviceTestController")
@RestController
public class ControllerEditorRegistrationAdviceTestController {

    private static final String TRANSIENT_PROPERTY_IGNORE_RESPONSE_FORMAT = "value=%s transientValue=%s";

    @PostMapping("convertEmptyStringToNull")
    public String convertEmptyStringToNull(String param) {
        return "value=" + param;
    }

    @PostMapping("ignoreTransientProperty")
    public String ignoreTransientProperty(ControllerEditorRegistrationAdviceTestRequest request) {
        return String.format(TRANSIENT_PROPERTY_IGNORE_RESPONSE_FORMAT, request.getProperty(), request.getTransientProperty());
    }
}
