package rs.edu.raf.banka1.responses;

import lombok.Data;

@Data
public class NewPasswordResponse {
    private Long userId;

    public NewPasswordResponse(Long userId) {
        this.userId = userId;
    }

    public NewPasswordResponse() {
    }
}
