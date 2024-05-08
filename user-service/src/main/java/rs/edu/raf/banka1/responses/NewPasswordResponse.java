package rs.edu.raf.banka1.responses;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewPasswordResponse {
    private Long userId;
    public NewPasswordResponse(Long userId) {
        this.userId = userId;
    }
}
