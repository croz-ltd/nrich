package net.croz.nrich.webmvc.advice.stub;

import lombok.Data;

@Data
public class ControllerEditorRegistrationAdviceTestRequest {

    private transient String transientProperty;

    private String property;

}
