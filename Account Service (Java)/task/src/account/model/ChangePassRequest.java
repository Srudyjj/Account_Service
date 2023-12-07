package account.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ChangePassRequest {

    public ChangePassRequest() {
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String newPassword;

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
