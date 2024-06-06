package account.model.entity;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "app_groups")
public class Group {

    public Group() {
    }

    public Group(String name) {
        this.name = name;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
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
}
