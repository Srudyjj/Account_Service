package account.service;

import account.model.ROLE;
import account.model.entity.AppUser;
import account.model.entity.Group;
import account.repository.AppUserRepository;
import account.repository.GroupRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    public static final int MAX_FAILED_ATTEMPTS = 5;

    private final AppUserRepository repository;

    private final PasswordEncoder passwordEncoder;

    private final BreachedPasswordsService passwordsService;

    private final GroupRepository groupRepository;

    public UserService(AppUserRepository repository, PasswordEncoder passwordEncoder, BreachedPasswordsService passwordsService, GroupRepository groupRepository) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.passwordsService = passwordsService;
        this.groupRepository = groupRepository;
    }

    public AppUser findUserByEmail(String email) {
        return repository
                .findAppUserByEmailIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public List<AppUser> getUsers() {
        return repository.findAllByOrderByIdAsc();
    }

    @Transactional
    public void deleteUser(AppUser user) {
        repository.delete(user);
    }

    @Transactional
    public AppUser updateUser(AppUser user) {
        return repository.save(user);
    }

    @Transactional
    public AppUser register(String name, String lastname, String email, String password) {
        Optional<AppUser> appUserByEmail = repository.findAppUserByEmailIgnoreCase(email);
        if (appUserByEmail.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User exist!");
        }
        passwordsService.validate(password);

        AppUser user = new AppUser(name, lastname, email.toLowerCase(), passwordEncoder.encode(password));
        setUserGroup(user);

        return repository.save(user);
    }

    @Transactional
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

    private void setUserGroup(AppUser appUser) {
        if(repository.count() == 0) {
            Group administrator = groupRepository.findByNameIgnoreCase(ROLE.ADMINISTRATOR).orElseThrow();
            appUser.addUserGroup(administrator);
        } else {
            Group administrator = groupRepository.findByNameIgnoreCase(ROLE.USER).orElseThrow();
            appUser.addUserGroup(administrator);
        }
    }

    @Transactional
    public void increaseFailedAttempts(AppUser user) {
        int newFailAttempts = user.getFailedLogInAttempt() + 1;
        user.setFailedLogInAttempt(newFailAttempts);
        repository.save(user);
    }

    @Transactional
    public void resetFailedAttempts(AppUser user) {
        user.setFailedLogInAttempt(0);
        repository.save(user);
    }

    @Transactional
    public void lock(AppUser user) {
        user.setLocked(true);
        repository.save(user);
    }

    @Transactional
    public void unLock(AppUser user) {
        user.setLocked(false);
        repository.save(user);
    }
}
