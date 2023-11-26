package account.service;

import account.model.AppUser;
import account.repository.AppUserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class RegistrationService {

    private static final String USER_ROLE = "USER";

    private final AppUserRepository repository;

    private final PasswordEncoder passwordEncoder;

    public RegistrationService(AppUserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public AppUser register(String name, String lastname, String email, String password) {
        Optional<AppUser> appUserByEmail = repository.findAppUserByEmailIgnoreCase(email);
        if (appUserByEmail.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User exist!");
        }
        return repository.save(new AppUser(name, lastname, email, passwordEncoder.encode(password), USER_ROLE));
    }
}
