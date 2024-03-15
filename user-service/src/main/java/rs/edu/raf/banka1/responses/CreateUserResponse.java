package rs.edu.raf.banka1.responses;

import lombok.Data;

@Data
public class CreateUserResponse {
    private Long userId;

    public CreateUserResponse(Long userId) {
        this.userId = userId;
    }
}
