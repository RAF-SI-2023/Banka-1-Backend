package rs.edu.raf.banka1.requests;

import lombok.Data;

@Data
public class CreateUserRequest {
    private String email;
    private String firstName;
    private String lastName;
    private String jmbg;
    private String position;
    private String phoneNumber;
    private boolean active;
}
