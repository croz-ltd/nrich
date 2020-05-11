package net.croz.nrich.validation.constraint.stub;

import lombok.Data;
import net.croz.nrich.validation.api.constraint.ValidFile;
import org.springframework.http.codec.multipart.FilePart;

@Data
public class ValidFileValidatorFilePartTestRequest {

    @ValidFile
    private final FilePart file;

}
