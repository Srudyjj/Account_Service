package account.security;

import account.model.ROLE;
import account.model.entity.AppUser;
import account.model.entity.Group;
import account.model.entity.SecurityEvent;
import account.repository.AppUserRepository;
import account.repository.GroupRepository;
import account.service.SecurityEventService;
import account.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationEvent;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.TimeZone;

@Component
public class AuthenticationEventsListener {

    private final SecurityEventService eventService;
    private final UserService userService;

    private final GroupRepository groupRepository;
    private final AppUserRepository userRepository;

    public AuthenticationEventsListener(SecurityEventService eventService, UserService userService, GroupRepository groupRepository, AppUserRepository userRepository) {
        this.eventService = eventService;
        this.userService = userService;
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
    }

    @EventListener
    public void onSuccess(AuthenticationSuccessEvent successEvent) {
        log(successEvent);
    }

//    @EventListener
//    public void onFailure(AbstractAuthenticationFailureEvent failureEvent) {
//
//        String username = (String) failureEvent.getAuthentication().getPrincipal();
//
//        if (failureEvent instanceof AuthenticationFailureBadCredentialsEvent){
//            SecurityEvent securityEvent = new SecurityEvent(
//                    toLocalDateTime(failureEvent.getTimestamp()),
//                    SecEvent.LOGIN_FAILED.toString(),
//                    username,
//                    getRequestURI(),
//                    getRequestURI());
//            eventService.addSecurityEvent(securityEvent);
//        }
//        log(failureEvent);
//    }

    @EventListener
    @Transactional
    public void onBadCredentialsEvent(AuthenticationFailureBadCredentialsEvent failureEvent) {

        log(failureEvent);
        String username = (String) failureEvent.getAuthentication().getPrincipal();
        Optional<AppUser> user = userRepository.findAppUserByEmailIgnoreCase(username);

        if (user.isEmpty()) {
            eventService.addSecurityEvent(new SecurityEvent(
                    toLocalDateTime(failureEvent.getTimestamp()),
                    SecEvent.LOGIN_FAILED.toString(),
                    username,
                    getRequestURI(),
                    getRequestURI()));
        } else {
            AppUser currentUser = user.get();
            Group administrator = groupRepository.findByNameIgnoreCase(ROLE.ADMINISTRATOR).orElseThrow();

            if (currentUser.getUserGroups().contains(administrator)) {
                eventService.addSecurityEvent(new SecurityEvent(
                        toLocalDateTime(failureEvent.getTimestamp()),
                        SecEvent.LOGIN_FAILED.toString(),
                        username,
                        getRequestURI(),
                        getRequestURI()));
                return;
            }

            if (!currentUser.isLocked()) {
                userService.increaseFailedAttempts(currentUser);
                eventService.addSecurityEvent(new SecurityEvent(
                        toLocalDateTime(failureEvent.getTimestamp()),
                        SecEvent.LOGIN_FAILED.toString(),
                        username,
                        getRequestURI(),
                        getRequestURI()));
            }

            if (currentUser.getFailedLogInAttempt() >= UserService.MAX_FAILED_ATTEMPTS) {
                eventService.addSecurityEvent(new SecurityEvent(
                        toLocalDateTime(failureEvent.getTimestamp()),
                        SecEvent.BRUTE_FORCE.toString(),
                        username,
                        getRequestURI(),
                        getRequestURI()));

                userService.lock(currentUser);
                eventService.addSecurityEvent(new SecurityEvent(
                        toLocalDateTime(failureEvent.getTimestamp()),
                        SecEvent.LOCK_USER.toString(),
                        username,
                        String.format("Lock user %s", username.toLowerCase()),
                        getRequestURI()));
            }
        }
    }



    private void log(AbstractAuthenticationEvent event) {
        LocalDateTime localDateTime = toLocalDateTime(event.getTimestamp());
        if (event instanceof AbstractAuthenticationFailureEvent) {
            System.out.println("Failed authentication attempt at: " + localDateTime);
        } else {
            System.out.println("Successful authentication attempt at: " + localDateTime);
        }
        System.out.println("User: " + event.getAuthentication().getName());
        System.out.println("Authorities: " + event.getAuthentication().getAuthorities());
        System.out.println("Details: " + event.getAuthentication().getDetails());
        System.out.println("Credentials: " + event.getAuthentication().getCredentials());
        System.out.println("Principal: " + event.getAuthentication().getPrincipal());
        System.out.println("Path: " + getRequestURI());
        System.out.println("Is authenticated: " + event.getAuthentication().isAuthenticated());
        if (event instanceof AbstractAuthenticationFailureEvent failureEvent) {
            System.out.println("Exception: " + failureEvent.getException());
        }
    }

    private LocalDateTime toLocalDateTime(long timestamp) {
        return LocalDateTime.ofInstant(
                Instant.ofEpochSecond(timestamp / 1000), TimeZone.getDefault().toZoneId()
        );
    }

    private static String getRequestURI() {
        ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        return attrs.getRequest().getRequestURI();
    }
}
