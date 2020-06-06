package net.croz.nrich.validation.constraint.stub;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.croz.nrich.validation.api.constraint.ValidFile;
import org.springframework.http.codec.multipart.FilePart;

@RequiredArgsConstructor
@Getter
public class ValidFileValidatorFilePartTestRequest {

    @ValidFile
    private final FilePart file;

}
