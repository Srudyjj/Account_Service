package account.security;

import account.model.entity.SecurityEvent;
import account.service.SecurityEventService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final SecurityEventService eventService;

    public CustomAccessDeniedHandler(SecurityEventService eventService) {
        this.eventService = eventService;
    }

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        response.sendError(HttpStatus.FORBIDDEN.value(), "Access Denied!");
        AppUserAdapter activeUser = (AppUserAdapter) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        eventService.addSecurityEvent(new SecurityEvent(
                SecEvent.ACCESS_DENIED.toString(),
                activeUser.getUsername(),
                request.getRequestURI(),
                request.getRequestURI()));
    }
}
