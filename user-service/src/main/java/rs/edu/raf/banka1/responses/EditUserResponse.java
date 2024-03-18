package rs.edu.raf.banka1.responses;

public class EditUserResponse {
    private Long userId;

    public EditUserResponse(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public EditUserResponse() {
    }
}
