package rs.edu.raf.banka1.responses;

import lombok.Data;
import java.util.List;
@Data
public class LoginResponse {
    private String jwt;
    private List<String> permissions;

    public LoginResponse(String jwt, List<String> permissions) {
        this.jwt = jwt;
        this.permissions = permissions;
    }
}
