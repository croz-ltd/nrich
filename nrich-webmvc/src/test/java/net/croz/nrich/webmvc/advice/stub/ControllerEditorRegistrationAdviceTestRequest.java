package net.croz.nrich.webmvc.advice.stub;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ControllerEditorRegistrationAdviceTestRequest {

    private transient String transientProperty;

    private String property;

}
