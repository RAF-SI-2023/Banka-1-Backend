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
}
