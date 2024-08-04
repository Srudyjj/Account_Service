package account.controller;

import account.model.entity.SecurityEvent;
import account.service.SecurityEventService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/security")
public class AuditorController {

    private final SecurityEventService eventService;

    public AuditorController(SecurityEventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/events/")
    public ResponseEntity<List<SecurityEvent>> getEvents() {
        return ResponseEntity.ok(eventService.getSecurityEvents());
    }


}
