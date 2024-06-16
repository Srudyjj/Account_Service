package account.controller;

import account.model.dto.DeleteUserResponse;
import account.model.dto.SingUpDTO;
import account.model.entity.AppUser;
import account.service.UserService;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/admin")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user")
    public ResponseEntity<List<SingUpDTO>> getUsers() {
        return ResponseEntity.ok(userService.getUsers()
                .stream()
                .map(SingUpDTO::new)
                .toList());
    }

    @DeleteMapping("/user/{email}")
    public ResponseEntity<DeleteUserResponse> deleteUser(@PathVariable @Email @Pattern(regexp = "^.+@acme\\.com$") String email) {
        AppUser user = userService.findUserByEmail(email);
        userService.deleteUser(user);
        return ResponseEntity.ok(new DeleteUserResponse(email));
    }
}
