package rs.edu.raf.banka1.responses;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangeOrderResponse {
    private Boolean success;
    private String message;

    public ChangeOrderResponse(Boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}
