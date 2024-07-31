package account.repository;

import account.model.entity.SecurityEvent;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SecurityEventRepository extends CrudRepository<SecurityEvent, Long> {

    List<SecurityEvent> findAllByOrderByIdAsc();
}
