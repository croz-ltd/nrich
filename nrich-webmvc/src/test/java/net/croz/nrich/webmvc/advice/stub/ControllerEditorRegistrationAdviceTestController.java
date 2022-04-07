package net.croz.nrich.webmvc.advice.stub;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("controller-editor-registration-advice-test-controller")
@RestController
public class ControllerEditorRegistrationAdviceTestController {

    private static final String TRANSIENT_PROPERTY_IGNORE_RESPONSE_FORMAT = "value=%s transientValue=%s";

    @PostMapping("empty-strings-to-null")
    public String emptyStringsToNull(String param) {
        return "value=" + param;
    }

    @PostMapping("transient-properties-serialization")
    public String transientPropertiesSerialization(ControllerEditorRegistrationAdviceTestRequest request) {
        return String.format(TRANSIENT_PROPERTY_IGNORE_RESPONSE_FORMAT, request.getProperty(), request.getTransientProperty());
    }
}
