package net.croz.nrich.validation.constraint.stub;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.croz.nrich.validation.api.constraint.ValidFile;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Getter
public class ValidFileValidatorMultipartFileTestRequest {

    @ValidFile(allowedContentTypeList = "text/plain", allowedExtensionList = "txt", allowedFileNameRegex = "(?U)[\\w-.]+")
    private final MultipartFile file;

}
