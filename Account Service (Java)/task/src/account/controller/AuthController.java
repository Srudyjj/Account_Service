package account.controller;

import account.model.dto.ChangePassRequest;
import account.model.dto.ChangePassResponse;
import account.service.RegistrationService;
import account.model.entity.AppUser;
import account.model.dto.SingUpDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
    public ResponseEntity<SingUpDTO> signup(@Valid @RequestBody SingUpDTO singUpDTO) {
        var name = singUpDTO.getName();
        var lastname = singUpDTO.getLastname();
        var email = singUpDTO.getEmail();
        var password = singUpDTO.getPassword();

        AppUser registered = registrationService.register(name, lastname, email, password);
        singUpDTO.setId(registered.getId());

        return ResponseEntity.ok(singUpDTO);
    }

    @PostMapping("/changepass")
    public ResponseEntity<ChangePassResponse> changepass(@AuthenticationPrincipal UserDetails details, @Valid @RequestBody ChangePassRequest request) {
        String email = registrationService.updatePassword(details.getUsername(), request.getNewPassword());
        return ResponseEntity.ok(new ChangePassResponse(email,
                "The password has been updated successfully"));
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
