package net.croz.nrich.validation.constraint.stub;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.croz.nrich.validation.api.constraint.ValidFileResolvable;
import org.springframework.http.codec.multipart.FilePart;

@RequiredArgsConstructor
@Getter
public class ValidFileResolvableValidatorFilePartTestRequest {

    @ValidFileResolvable
    private final FilePart file;

}
