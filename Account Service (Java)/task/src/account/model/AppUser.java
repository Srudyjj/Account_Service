package account.model;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "app_user")
public class AppUser {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String lastname;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String authority;

    public AppUser() {
    }

    public AppUser(String name, String lastname, String email, String password, String authority) {
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.authority = authority;
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

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
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
