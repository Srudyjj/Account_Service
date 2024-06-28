package account.controller;

import account.model.ROLE;
import account.model.dto.ChangeUserRole;
import account.model.dto.DeleteUserResponse;
import account.model.dto.SingUpDTO;
import account.model.entity.AppUser;
import account.model.entity.Group;
import account.repository.GroupRepository;
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
    private final GroupRepository groupRepository;

    public AdminController(UserService userService, GroupRepository groupRepository) {
        this.userService = userService;
        this.groupRepository = groupRepository;
    }

    @GetMapping("/user/**")
    public ResponseEntity<List<SingUpDTO>> getUsers() {
        return ResponseEntity.ok(userService.getUsers()
                .stream()
                .map(SingUpDTO::new)
                .toList());
    }

    @DeleteMapping(value = {"/user/{email}", "/user/"})
    public ResponseEntity<DeleteUserResponse> deleteUser(@PathVariable @Email @Pattern(regexp = "^.+@acme\\.com$") String email) {
        AppUser user = userService.findUserByEmail(email);

        if (user.getUserGroups().stream().anyMatch(group -> group.getName().equals(ROLE.ADMINISTRATOR))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't remove ADMINISTRATOR role!");
        }

        userService.deleteUser(user);
        return ResponseEntity.ok(new DeleteUserResponse(email));
    }

    @PutMapping("/user/role")
    public ResponseEntity<SingUpDTO> changeUserRole(@RequestBody ChangeUserRole userRole) {
        AppUser user = userService.findUserByEmail(userRole.user());
        Group group = groupRepository.findByNameIgnoreCase("ROLE_" + userRole.role())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found!"));

        boolean isUserAdmin = user.getUserGroups()
                .stream()
                .anyMatch(g -> g.getName().equals(ROLE.ADMINISTRATOR));

        boolean isBusinessUser = user.getUserGroups()
                .stream()
                .noneMatch(g -> g.getName().equals(ROLE.ADMINISTRATOR));

        if (userRole.operation() == ChangeUserRole.OPERATION.GRANT) {
            if (isUserAdmin && !group.getName().equals(ROLE.ADMINISTRATOR) ||
                    isBusinessUser && group.getName().equals(ROLE.ADMINISTRATOR)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The user cannot combine administrative and business roles!");
            }

            user.addUserGroup(group);
        } else if (userRole.operation() == ChangeUserRole.OPERATION.REMOVE) {
            if (group.getName().equals(ROLE.ADMINISTRATOR)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't remove ADMINISTRATOR role!");
            }

            if (!user.getUserGroups().contains(group)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The user does not have a role!");
            }

            if (user.getUserGroups().size() <= 1) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The user must have at least one role!");
            }

            user.removeUserGroup(group);
        }

        AppUser updatedUser = userService.updateUser(user);

        return ResponseEntity.ok(new SingUpDTO(updatedUser));
    }
}
