package account.controller;


import account.model.AppUser;
import account.model.SingUpDTO;
import account.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/empl")
public class PaymentController {

    private final UserService userService;

    public PaymentController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/payment")
    public ResponseEntity<SingUpDTO> payment(@AuthenticationPrincipal UserDetails details) {
        AppUser user = userService.findUserByEmail(details.getUsername());
        return ResponseEntity.ok(new SingUpDTO(user.getId(), user.getName(), user.getLastname(), user.getEmail()));
    }
}
