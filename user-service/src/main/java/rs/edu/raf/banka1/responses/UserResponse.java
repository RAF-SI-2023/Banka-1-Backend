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
    private String jmbg;
    private String position;
    private String phoneNumber;
    private Boolean active;
}

