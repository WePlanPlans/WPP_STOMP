package org.tenten.tentenstomp.global.util;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DefaultController {
    @GetMapping("/info")
    public ResponseEntity<?> info() {
        return ResponseEntity.ok("SUCCESS");
    }
}
