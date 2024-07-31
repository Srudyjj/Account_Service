package account.service;

import account.model.entity.SecurityEvent;
import account.repository.SecurityEventRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SecurityEventService {

    private final SecurityEventRepository eventRepository;

    public SecurityEventService(SecurityEventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public void addSecurityEvent(SecurityEvent securityEvent){
        eventRepository.save(securityEvent);
    }

    public List<SecurityEvent> getSecurityEvents(){
        return eventRepository.findAllByOrderByIdAsc();
    }
}
