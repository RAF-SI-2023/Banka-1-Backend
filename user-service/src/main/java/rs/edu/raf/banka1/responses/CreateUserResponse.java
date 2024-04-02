package rs.edu.raf.banka1.responses;

public class CreateUserResponse {
    private Long userId;
    private String message;

    public CreateUserResponse() {
        // Default constructor
    }
    //@JsonCreator
    public CreateUserResponse(Long userId) {
        this.userId = userId;
        message = "";
    }

    public CreateUserResponse(Long userId, String message) {
        this.userId = userId;
        this.message = message;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
