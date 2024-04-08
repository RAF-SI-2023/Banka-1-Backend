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
public class CreateTransferRequest {
    private String senderAccountNumber;
    private String senderName;
    private String recipientName;
    private String recipientAccountNumber;
    private Double amount;
    private String paymentCode;
    private String model;
    private String referenceNumber;
    private String paymentPurpose;
}
