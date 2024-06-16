package account.controller;

import account.model.dto.SingUpDTO;
import account.model.entity.AppUser;
import account.service.RegistrationService;
import account.service.UserService;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/admin")
public class AdminController {

    private final RegistrationService registrationService;

    private final UserService userService;

    public AdminController(RegistrationService registrationService, UserService userService) {
        this.registrationService = registrationService;
        this.userService = userService;
    }

    @GetMapping("/user")
    public ResponseEntity<List<SingUpDTO>> getUsers() {
        return ResponseEntity.ok(registrationService.getUsers()
                .stream()
                .map(SingUpDTO::new)
                .toList());
    }

    @DeleteMapping("/user/{email}")
    public ResponseEntity<List<SingUpDTO>> deleteUser(@PathVariable @Email @Pattern(regexp = "^.+@acme\\.com$") String email) {
        AppUser user = userService.findUserByEmail(email);
        return ResponseEntity.ok(registrationService.getUsers()
                .stream()
                .map(SingUpDTO::new)
                .toList());
    }
}
