package net.croz.nrich.validation.constraint.stub;

import lombok.Data;
import net.croz.nrich.validation.api.constraint.ValidFile;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ValidFileValidatorMultipartFileTestRequest {

    @ValidFile
    private final MultipartFile file;

}
