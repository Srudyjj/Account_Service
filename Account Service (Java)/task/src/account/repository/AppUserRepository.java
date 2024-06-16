package account.repository;

import account.model.entity.AppUser;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface AppUserRepository extends CrudRepository<AppUser, Long> {

    Optional<AppUser> findAppUserByEmailIgnoreCase(String email);

    List<AppUser> findAllByOrderByIdAsc();
}
