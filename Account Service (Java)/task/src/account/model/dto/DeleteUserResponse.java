package account.model.dto;

public record DeleteUserResponse(String user, String status) {
    public DeleteUserResponse(String user) {
        this(user, "Deleted successfully!");
    }
}
