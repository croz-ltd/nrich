package net.croz.nrich.validation.constraint.stub;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.croz.nrich.validation.api.constraint.ValidFileResolvable;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Getter
public class ValidFileResolvableValidatorMultipartFileCustomTestRequest {

    @ValidFileResolvable(
            allowedContentTypeListPropertyName = "my.custom.validation.file.allowed-content-type-list",
            allowedExtensionListPropertyName = "my.custom.validation.file.allowed-extension-list",
            allowedFileNameRegexPropertyName = "my.custom.validation.file.allowed-file-name-regex"
    )
    private final MultipartFile file;
}
