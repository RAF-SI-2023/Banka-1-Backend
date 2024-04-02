package rs.edu.raf.banka1.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRecipientDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String bankAccountNumber;
}
