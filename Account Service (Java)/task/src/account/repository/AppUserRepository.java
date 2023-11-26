package account.repository;

import account.model.AppUser;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface AppUserRepository extends CrudRepository<AppUser, Long> {

    Optional<AppUser> findAppUserByEmailIgnoreCase(String email);
}
