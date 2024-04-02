package rs.edu.raf.banka1.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EditPaymentRecipientRequest {
    private Long id;
    private String firstName;
    private String lastName;
    private String bankAccountNumber;
}
