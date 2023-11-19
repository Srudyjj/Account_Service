package account.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;

public class SingUpDTO {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    private String name;
    private String lastname;
    private String email;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    public SingUpDTO() {
    }

    public SingUpDTO(String name, String lastname, String email, String password) {
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
    }

    public SingUpDTO(Long id, String name, String lastname, String email) {
        this.id = id;
        this.name = name;
        this.lastname = lastname;
        this.email = email;
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
}
