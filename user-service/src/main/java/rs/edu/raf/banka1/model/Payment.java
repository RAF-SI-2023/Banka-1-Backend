package rs.edu.raf.banka1.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    private String senderAccountOwnerName;
//    private String senderAccountNumber;

    @ManyToOne
    @JoinColumn(name = "sender_bankaccount_id", referencedColumnName = "id")
    private BankAccount senderBankAccount;

    private String recipientName;
    private String recipientAccountNumber;
    private Double amount;
    private String paymentCode;
    private String model;
    private String referenceNumber;
    private TransactionStatus status;
    private Double commissionFee;
    private Long dateOfPayment;
    private String channel;
    private String paymentPurpose;

}
