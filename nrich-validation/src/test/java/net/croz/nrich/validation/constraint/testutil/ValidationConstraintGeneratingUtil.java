package net.croz.nrich.validation.constraint.testutil;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class ValidationConstraintGeneratingUtil {

    private ValidationConstraintGeneratingUtil() {
    }

    public static FilePart filePart(final String fileName, final MediaType mediaType) {
        final FilePart filePart = mock(FilePart.class);
        final HttpHeaders headers = new HttpHeaders();

        headers.setContentType(mediaType);

        when(filePart.filename()).thenReturn(fileName);
        when(filePart.headers()).thenReturn(headers);

        return filePart;
    }
}
