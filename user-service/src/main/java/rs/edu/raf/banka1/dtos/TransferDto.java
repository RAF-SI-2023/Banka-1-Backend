package rs.edu.raf.banka1.dtos;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rs.edu.raf.banka1.model.Currency;
import rs.edu.raf.banka1.model.TransactionStatus;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransferDto {
   private Long id;
   private String senderName;
   private String senderAccountNumber;
   private String recipientAccountNumber;
   private Double amount;
   private Double convertedAmount; //Konvertovani iznos sa prvog računa
   private Double exchangeRate;
   private TransactionStatus status;
   private Double commission;
   private Long dateOfPayment;
   private String previousCurrency;
   private String exchangedTo;
   private Double profit;
}
