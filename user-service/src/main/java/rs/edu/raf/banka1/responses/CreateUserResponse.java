package rs.edu.raf.banka1.responses;

import lombok.Data;

@Data
public class CreateUserResponse {
    private String response;
    public CreateUserResponse(String response) {
        this.response = response;
    }
}
