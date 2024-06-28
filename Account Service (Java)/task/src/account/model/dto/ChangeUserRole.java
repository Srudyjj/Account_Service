package account.model.dto;

import account.model.entity.AppUser;

public record ChangeUserRole(String user, String role, OPERATION operation) {

    public enum OPERATION {
        GRANT, REMOVE
    }
}
