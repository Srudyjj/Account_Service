package account.controller;

import account.service.RegistrationService;
import account.model.AppUser;
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
    
    private final RegistrationService registrationService;

    public AuthController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping("/signup")
    public ResponseEntity<SingUpDTO> signup(@RequestBody SingUpDTO singUpDTO) {
        var name = notEmpty(singUpDTO.getName());
        var lastname = notEmpty(singUpDTO.getLastname());
        var email = validEmail(notEmpty(singUpDTO.getEmail()));
        var password = notEmpty(singUpDTO.getPassword());

        AppUser registered = registrationService.register(name, lastname, email, password);
        singUpDTO.setId(registered.getId());

        return ResponseEntity.ok(singUpDTO);
    }

    private static String notEmpty(String string) {
        if (string == null || string.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return string;
    }

    private static String validEmail(String string) {
        if (!string.contains("@") || !string.endsWith("@acme.com")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return string;
    }
}
