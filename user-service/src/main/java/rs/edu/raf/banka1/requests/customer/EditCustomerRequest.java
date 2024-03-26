package rs.edu.raf.banka1.requests.customer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EditCustomerRequest {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String gender;
    private String address;
    private String position; // for frontend purposes ???
    private String phoneNumber;
    private Boolean isActive;
    private List<String> permissions;
}
