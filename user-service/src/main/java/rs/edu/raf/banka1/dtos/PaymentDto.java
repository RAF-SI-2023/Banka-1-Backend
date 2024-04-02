package rs.edu.raf.banka1.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rs.edu.raf.banka1.model.TransactionStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDto {
    private Long id;
    private String senderAccountOwnerName;
    private String senderAccountNumber;
    private String recipientAccountOwnerName;
    private String recipientAccountNumber;
    private Double amount;
    private String paymentCode;
    private String model;
    private String referenceNumber;
    private TransactionStatus status;
    private Double commissionFee;
    private Long dateOfPayment;
    private String channel;
}
