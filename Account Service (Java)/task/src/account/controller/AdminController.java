package account.controller;

import account.model.ROLE;
import account.model.dto.*;
import account.model.entity.AppUser;
import account.model.entity.Group;
import account.model.entity.SecurityEvent;
import account.repository.GroupRepository;
import account.security.SecEvent;
import account.service.SecurityEventService;
import account.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("api/admin")
public class AdminController {

    private final UserService userService;
    private final GroupRepository groupRepository;

    private final SecurityEventService eventService;

    public AdminController(UserService userService, GroupRepository groupRepository, SecurityEventService eventService) {
        this.userService = userService;
        this.groupRepository = groupRepository;
        this.eventService = eventService;
    }

    @GetMapping("/user/**")
    public ResponseEntity<List<SingUpDTO>> getUsers() {
        return ResponseEntity.ok(userService.getUsers().stream().map(SingUpDTO::new).toList());
    }

    @DeleteMapping(value = {"/user/{email}", "/user/"})
    public ResponseEntity<DeleteUserResponse> deleteUser(@PathVariable @Email @Pattern(regexp = "^.+@acme\\.com$") String email,
                                                         @AuthenticationPrincipal UserDetails subjectUserDetails,
                                                         HttpServletRequest request) {
        AppUser user = userService.findUserByEmail(email);

        if (user.getUserGroups().stream().anyMatch(group -> group.getName().equals(ROLE.ADMINISTRATOR))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't remove ADMINISTRATOR role!");
        }

        userService.deleteUser(user);
        eventService.addSecurityEvent(new SecurityEvent(
                SecEvent.DELETE_USER.toString(),
                subjectUserDetails.getUsername(),
                user.getEmail(),
                request.getRequestURI()));
        return ResponseEntity.ok(new DeleteUserResponse(email));
    }

    @PutMapping("/user/role")
    public ResponseEntity<SingUpDTO> changeUserRole(@RequestBody ChangeUserRole userRole, @AuthenticationPrincipal UserDetails subjectUserDetails, HttpServletRequest request) {
        AppUser user = userService.findUserByEmail(userRole.user());
        Group group = groupRepository.findByNameIgnoreCase("ROLE_" + userRole.role()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found!"));

        boolean isUserAdmin = user.getUserGroups().stream().anyMatch(g -> g.getName().equals(ROLE.ADMINISTRATOR));

        boolean isBusinessUser = user.getUserGroups().stream().noneMatch(g -> g.getName().equals(ROLE.ADMINISTRATOR));

        if (userRole.operation() == ChangeUserRole.OPERATION.GRANT) {
            if (isUserAdmin && !group.getName().equals(ROLE.ADMINISTRATOR) || isBusinessUser && group.getName().equals(ROLE.ADMINISTRATOR)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The user cannot combine administrative and business roles!");
            }

            user.addUserGroup(group);
            eventService.addSecurityEvent(new SecurityEvent(SecEvent.GRANT_ROLE.toString(), subjectUserDetails.getUsername(), String.format("Grant role %s to %s", userRole.role(), user.getEmail()), request.getRequestURI()));
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
            eventService.addSecurityEvent(new SecurityEvent(SecEvent.REMOVE_ROLE.toString(), subjectUserDetails.getUsername(), String.format("Remove role %s from %s", userRole.role(), user.getEmail()), request.getRequestURI()));
        }

        AppUser updatedUser = userService.updateUser(user);

        return ResponseEntity.ok(new SingUpDTO(updatedUser));
    }

    @PutMapping("/user/access")
    public ResponseEntity<Status> lockUnlockUser(@RequestBody LockUnlockUserDTO userOperation, @AuthenticationPrincipal UserDetails subjectUserDetails, HttpServletRequest request) {
        AppUser user = userService.findUserByEmail(userOperation.user());
        boolean isUserAdmin = user.getUserGroups().stream().anyMatch(g -> g.getName().equals(ROLE.ADMINISTRATOR));

        if (userOperation.operation() == LockUnlockUserDTO.OPERATION.LOCK) {
            if (isUserAdmin) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't lock the ADMINISTRATOR!");
            }
            userService.lock(user);
            eventService.addSecurityEvent(new SecurityEvent(SecEvent.LOCK_USER.toString(), subjectUserDetails.getUsername(), String.format("Lock user %s", user.getEmail()), request.getRequestURI()));
            return ResponseEntity.ok(new Status(String.format("User %s %s!", user.getEmail(), "locked")));
        } else if (userOperation.operation() == LockUnlockUserDTO.OPERATION.UNLOCK) {
            userService.unLock(user);
            eventService.addSecurityEvent(new SecurityEvent(SecEvent.UNLOCK_USER.toString(), subjectUserDetails.getUsername(), String.format("Unlock user %s", user.getEmail()), request.getRequestURI()));
            return ResponseEntity.ok(new Status(String.format("User %s %s!", user.getEmail(), "unlocked")));
        }

        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong operation type" + userOperation.operation());
    }
}
