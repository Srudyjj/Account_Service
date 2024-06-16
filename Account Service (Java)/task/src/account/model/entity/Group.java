package account.model.entity;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "app_groups",
        uniqueConstraints = @UniqueConstraint(
            columnNames = {"name", "code"},
            name = "unique_group"))
public class Group {

    public Group() {
    }

    public Group(String code, String name) {
        this.code = code;
        this.name = name;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String code;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinTable(
            name = "user_groups",
            joinColumns = @JoinColumn(name = "app_user_id", foreignKey = @ForeignKey(name = "app_user_group_fk")),
            inverseJoinColumns = @JoinColumn(name = "group_id", foreignKey = @ForeignKey(name = "group_app_user_fk")))
    private Set<AppUser> users;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<AppUser> getUsers() {
        return users;
    }

    private void setUsers(Set<AppUser> users) {
        this.users = users;
    }

    public void addUser(AppUser user) {
        this.users.add(user);
        user.getUserGroups().add(this);
    }

    public void removeUser(AppUser user) {
        this.users.remove(user);
        user.getUserGroups().remove(this);
    }
}
