package account.repository;

import account.model.entity.Group;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface GroupRepository extends CrudRepository<Group, Long> {

    Optional<Group> findByNameIgnoreCase(String name);
}
