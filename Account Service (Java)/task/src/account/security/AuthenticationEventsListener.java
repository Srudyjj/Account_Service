package account.security;

import account.model.entity.SecurityEvent;
import account.service.SecurityEventService;
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
import java.util.TimeZone;

@Component
public class AuthenticationEventsListener {

    private final SecurityEventService eventService;

    public AuthenticationEventsListener(SecurityEventService eventService) {
        this.eventService = eventService;
    }

    @EventListener
    public void onSuccess(AuthenticationSuccessEvent successEvent) {
        log(successEvent);
    }

    @EventListener
    public void onFailure(AbstractAuthenticationFailureEvent failureEvent) {

        String username = (String) failureEvent.getAuthentication().getPrincipal();

        if (failureEvent instanceof AuthenticationFailureBadCredentialsEvent){
            SecurityEvent securityEvent = new SecurityEvent(
                    toLocalDateTime(failureEvent.getTimestamp()),
                    SecEvent.LOGIN_FAILED.toString(),
                    username,
                    getRequestURI(),
                    getRequestURI());
            eventService.addSecurityEvent(securityEvent);
        }
        log(failureEvent);
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
