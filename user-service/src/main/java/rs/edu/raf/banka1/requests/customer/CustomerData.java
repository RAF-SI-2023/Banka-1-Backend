package rs.edu.raf.banka1.requests.customer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerData {
    private String firstName;
    private String lastName;
    private String position; // for frontend purposes
    private Long dateOfBirth;
    private String gender;
    private String email;
    private String phoneNumber;
    private String address;
    private String jmbg;
}
