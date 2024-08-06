package account.controller;

import account.model.dto.ChangePassRequest;
import account.model.dto.ChangePassResponse;
import account.model.entity.Group;
import account.model.entity.SecurityEvent;
import account.security.SecEvent;
import account.service.SecurityEventService;
import account.service.UserService;
import account.model.entity.AppUser;
import account.model.dto.SingUpDTO;
import jakarta.servlet.http.HttpServletRequest;
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
    
    private final UserService userService;

    private final SecurityEventService eventService;

    public AuthController(UserService userService, SecurityEventService eventService) {
        this.userService = userService;
        this.eventService = eventService;
    }

    @PostMapping("/signup")
    public ResponseEntity<SingUpDTO> signup(@Valid @RequestBody SingUpDTO singUpDTO, HttpServletRequest request) {
        var name = singUpDTO.getName();
        var lastname = singUpDTO.getLastname();
        var email = singUpDTO.getEmail();
        var password = singUpDTO.getPassword();

        AppUser registered = userService.register(name, lastname, email.toLowerCase(), password);
        singUpDTO.setId(registered.getId());
        singUpDTO.setRoles(registered.getUserGroups().stream().map(Group::getName).toList());

        eventService.addSecurityEvent(new SecurityEvent(SecEvent.CREATE_USER.toString(), registered.getEmail(), request.getRequestURI()));

        return ResponseEntity.ok(singUpDTO);
    }

    @PostMapping("/changepass")
    public ResponseEntity<ChangePassResponse> changepass(@AuthenticationPrincipal UserDetails details,
                                                         @Valid @RequestBody ChangePassRequest request,
                                                         HttpServletRequest httpServletRequest) {
        String email = userService.updatePassword(details.getUsername(), request.getNewPassword());
        eventService.addSecurityEvent(new SecurityEvent(
                SecEvent.CHANGE_PASSWORD.toString(),
                details.getUsername(),
                details.getUsername(),
                httpServletRequest.getRequestURI()));
        return ResponseEntity.ok(new ChangePassResponse(email,
                "The password has been updated successfully"));
    }
}
