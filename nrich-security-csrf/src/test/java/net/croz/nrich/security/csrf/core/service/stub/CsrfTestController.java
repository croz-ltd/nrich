package net.croz.nrich.security.csrf.core.service.stub;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("secured")
@RestController
public class CsrfTestController {

    @PostMapping("url")
    public String url() {
        return "result";
    }
}
