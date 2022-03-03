package net.croz.nrich.validation.constraint.stub;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.croz.nrich.validation.api.constraint.ValidFileResolvable;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Getter
public class ValidFileResolvableValidatorEmptyPropertyTestRequest {

    @ValidFileResolvable(allowedContentTypeListPropertyName = "", allowedExtensionListPropertyName = "", allowedFileNameRegexPropertyName = "")
    private final MultipartFile file;

}
