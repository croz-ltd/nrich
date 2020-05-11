package net.croz.nrich.notification.stub;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Setter
@Getter
public class NotificationResolverServiceTestWithoutMessageRequest {

    @NotNull
    private String name;

}
