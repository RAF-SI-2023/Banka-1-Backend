package rs.edu.raf.banka1.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Transfer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_bankaccount_id", referencedColumnName = "id")
    private BankAccount senderBankAccount;
    @ManyToOne
    @JoinColumn(name = "recipient_bankaccount_id", referencedColumnName = "id")
    private BankAccount recipientBankAccount;

    private Double amount;
    private Double convertedAmount; //Konvertovani iznos sa prvog računa
    private Double exchangeRate;
    private Double commission;
    private TransactionStatus status;
    private Long dateOfPayment;

    @ManyToOne
    @JoinColumn(name = "currencyFrom_id", referencedColumnName = "id")
    private Currency currencyFrom;

    @ManyToOne
    @JoinColumn(name = "currencyTo_id", referencedColumnName = "id")
    private Currency currencyTo;

    public static Double calculateCommission(Double amount) {
        return commissionPercentage() * amount;
    }

    public static double commissionPercentage() {
        return 0.01;
    }
}
