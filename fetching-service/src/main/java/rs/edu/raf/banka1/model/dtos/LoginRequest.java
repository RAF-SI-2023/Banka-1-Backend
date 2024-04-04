package rs.edu.raf.banka1.model.dtos;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}
