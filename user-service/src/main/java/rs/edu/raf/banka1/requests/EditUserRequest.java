package rs.edu.raf.banka1.requests;

import lombok.Data;
import rs.edu.raf.banka1.model.Permission;

import java.util.Set;

@Data
public class EditUserRequest {
    private Long userId;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String jmbg;
    private String position;
    private String phoneNumber;
    private boolean isActive;
    private Set<String> permissions;
}
