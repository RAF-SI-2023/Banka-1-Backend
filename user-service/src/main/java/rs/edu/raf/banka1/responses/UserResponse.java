package rs.edu.raf.banka1.responses;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponse {
    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
}

