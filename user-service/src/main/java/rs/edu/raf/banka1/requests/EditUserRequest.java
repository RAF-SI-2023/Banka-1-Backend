package rs.edu.raf.banka1.requests;

import lombok.*;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
    private List<String> permissions;
}
