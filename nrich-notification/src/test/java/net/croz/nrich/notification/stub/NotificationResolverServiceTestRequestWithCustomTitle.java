package net.croz.nrich.notification.stub;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Future;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Instant;

@Setter
@Getter
public class NotificationResolverServiceTestRequestWithCustomTitle {

    @NotNull
    private String name;

    @Size(min = 1, max = 5)
    private String lastName;

    @Future
    private Instant timestamp;

    @Min(10)
    private Integer value;

}
