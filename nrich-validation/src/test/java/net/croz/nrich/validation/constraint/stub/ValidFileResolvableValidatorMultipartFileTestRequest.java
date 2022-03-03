package net.croz.nrich.validation.constraint.stub;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.croz.nrich.validation.api.constraint.ValidFileResolvable;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Getter
public class ValidFileResolvableValidatorMultipartFileTestRequest {

    @ValidFileResolvable
    private final MultipartFile file;

}
