package rs.edu.raf.banka1.responses;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CancelOrderResponse {
    private Boolean success;
    private String message;

    public CancelOrderResponse(Boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}
