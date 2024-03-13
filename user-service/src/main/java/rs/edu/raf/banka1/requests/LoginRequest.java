package rs.edu.raf.banka1.requests;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;

}
