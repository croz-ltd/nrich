package net.croz.nrich.notification.api.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class ValidationError {

    public static final String CONTAINING_OBJECT_NAME = "CONTAINING_OBJECT";

    // if occurred on field then the name of the field otherwise it is CONTAINING_OBJECT string
    private final String objectName;

    private final List<String> errorMessageList;

}
