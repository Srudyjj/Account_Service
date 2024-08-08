package account.model.dto;

public record LockUnlockUserDTO(String user, OPERATION operation) {

    public enum OPERATION {
        LOCK, UNLOCK
    }
}
