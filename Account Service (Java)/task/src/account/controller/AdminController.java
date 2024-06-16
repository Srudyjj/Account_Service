package account.controller;

import account.model.ROLE;
import account.model.dto.DeleteUserResponse;
import account.model.dto.SingUpDTO;
import account.model.entity.AppUser;
import account.service.UserService;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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

        if (user.getUserGroups().stream().anyMatch(group -> group.getName().equals(ROLE.ADMINISTRATOR))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't remove ADMINISTRATOR role!");
        }

        userService.deleteUser(user);
        return ResponseEntity.ok(new DeleteUserResponse(email));
    }
}
