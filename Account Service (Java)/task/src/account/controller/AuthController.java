package account.controller;

import account.model.SingUpDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @PostMapping("/signup")
    public ResponseEntity<SingUpDTO> signup(@RequestBody SingUpDTO singUpDTO) {
        var name = notEmpty(singUpDTO.getName());
        var lastname = notEmpty(singUpDTO.getLastname());
        var email = notEmpty(singUpDTO.getEmail());
        if (!email.endsWith("@acme.com")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        var password = notEmpty(singUpDTO.getPassword());
        return ResponseEntity.ok(new SingUpDTO(name, lastname, email, password));
    }

    private static String notEmpty(String string) {
        if (string == null || string.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return string;
    }
}
