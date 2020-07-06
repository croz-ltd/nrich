package net.croz.nrich.validation.constraint.stub;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.croz.nrich.validation.api.constraint.ValidFile;
import org.springframework.http.codec.multipart.FilePart;

@RequiredArgsConstructor
@Getter
public class ValidFileValidatorFilePartTestRequest {

    @ValidFile(allowedContentTypeList = "text/plain", allowedExtensionList = "txt", allowedFileNameRegex = "(?U)[\\w-.]+")
    private final FilePart file;

}
