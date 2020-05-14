package net.croz.nrich.webmvc.advice.stub;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class NotificationErrorHandlingRestControllerAdviceTestWithoutMessageRequest {

    @NotNull
    private String name;

}
