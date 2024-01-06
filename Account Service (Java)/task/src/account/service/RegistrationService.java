package account.service;

import account.model.entity.AppUser;
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

    private final BreachedPasswordsService passwordsService;

    public RegistrationService(AppUserRepository repository, PasswordEncoder passwordEncoder, BreachedPasswordsService passwordsService) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.passwordsService = passwordsService;
    }

    public AppUser register(String name, String lastname, String email, String password) {
        Optional<AppUser> appUserByEmail = repository.findAppUserByEmailIgnoreCase(email);
        if (appUserByEmail.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User exist!");
        }
        passwordsService.validate(password);

        return repository.save(new AppUser(name, lastname, email.toLowerCase(), passwordEncoder.encode(password), USER_ROLE));
    }

    public String updatePassword(String email, String password) {
        AppUser appUser = repository.findAppUserByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "User doesn't exist!"));

        passwordsService.validate(password);

        if (passwordEncoder.matches(password, appUser.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The passwords must be different!");
        }

        appUser.setPassword(passwordEncoder.encode(password));
        repository.save(appUser);
        return appUser.getEmail();
    }
}
