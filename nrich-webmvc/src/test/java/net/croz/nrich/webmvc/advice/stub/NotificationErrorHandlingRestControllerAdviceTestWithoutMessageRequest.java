package net.croz.nrich.webmvc.advice.stub;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Setter
@Getter
public class NotificationErrorHandlingRestControllerAdviceTestWithoutMessageRequest {

    @NotNull
    private String name;

}
