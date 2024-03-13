package rs.edu.raf.banka1.responses;

import lombok.Getter;
import lombok.Setter;
import rs.edu.raf.banka1.model.Permission;

import java.util.Set;

@Getter
@Setter
public class UserResponse {
    private String UserId;
    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private String jmbg;
    private String position;
    private String phoneNumber;
    private Boolean active;
    private Set<Permission> permissions;
}

