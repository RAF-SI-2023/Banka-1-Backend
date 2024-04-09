package rs.edu.raf.banka1.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//CURRENCY
//pravljen po uzoru na Detalji transfera str 92
public class CreateTransferRequest {
    private String senderAccountNumber;
    private String recipientAccountNumber;
    private Double amount;
}
