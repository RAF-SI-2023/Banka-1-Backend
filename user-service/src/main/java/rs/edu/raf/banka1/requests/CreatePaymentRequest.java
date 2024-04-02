package rs.edu.raf.banka1.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreatePaymentRequest {
    private String senderAccountNumber;
    private String recipientName;
    private String recipientAccountNumber;
    private double amount;
    private String paymentCode;
    private String model;
    private String referenceNumber;
    private String paymentPurpose;
}
