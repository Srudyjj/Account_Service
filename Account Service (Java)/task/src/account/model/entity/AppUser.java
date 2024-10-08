package account.model.entity;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "app_user",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"email"},
                name = "unique_email"))
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String lastname;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column
    private boolean isLocked;

    @Column
    private int failedLogInAttempt;

    @ManyToMany(mappedBy = "users", fetch = FetchType.EAGER)
    private Set<Group> userGroups = new HashSet<>();

    public AppUser() {
    }

    public AppUser(String name, String lastname, String email, String password) {
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }

    public int getFailedLogInAttempt() {
        return failedLogInAttempt;
    }

    public void setFailedLogInAttempt(int failedLogInAttempt) {
        this.failedLogInAttempt = failedLogInAttempt;
    }

    public Set<Group> getUserGroups() {
        return userGroups;
    }

    private void setUserGroups(Set<Group> userGroups) {
        this.userGroups = userGroups;
    }

    public void addUserGroup(Group group) {
        group.addUser(this);
    }

    public void removeUserGroup(Group group) {
        group.removeUser(this);
    }

    @PreRemove
    private void removeGroupsAssociations() {
        for (Group group : userGroups) {
            group.removeUser(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AppUser user = (AppUser) o;
        return Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }
}
