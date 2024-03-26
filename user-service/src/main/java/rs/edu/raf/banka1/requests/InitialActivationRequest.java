package rs.edu.raf.banka1.requests;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InitialActivationRequest {
    private String email;
    private String phoneNumber;
    private String accountNumber;
}
