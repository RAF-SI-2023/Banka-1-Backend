package rs.edu.raf.banka1.responses;

public class CreateUserResponse {
    private Long userId;

    public CreateUserResponse() {
        // Default constructor
    }
    //@JsonCreator
    public CreateUserResponse(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
