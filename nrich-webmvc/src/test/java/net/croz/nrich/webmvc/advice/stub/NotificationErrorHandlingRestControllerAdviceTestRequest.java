package net.croz.nrich.webmvc.advice.stub;

import lombok.Data;

import javax.validation.constraints.Future;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Instant;

@Data
public class NotificationErrorHandlingRestControllerAdviceTestRequest {

    @NotNull
    private String name;

    @Size(min = 1, max = 5)
    private String lastName;

    @Future
    private Instant timestamp;

    @Min(10)
    private Integer value;

}