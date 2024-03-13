package rs.edu.raf.banka1.responses;

import lombok.Data;

@Data
public class ActivateAccountResponse {
    private Long userId;

    public ActivateAccountResponse(Long userId) {
        this.userId = userId;
    }
}
