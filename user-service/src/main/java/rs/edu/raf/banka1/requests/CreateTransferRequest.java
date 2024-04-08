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
    private String senderName;
    private String recipientName;
    private String recipientAccountNumber;
    //htela sam da kazem da ovo ne treba jer je menjacnica za jednu
    //te istu osobu ali moramo da imamo distinkciju izmedju
    // tekuceg i deviznog

    private Double amount;
    private String paymentCode;
    private String model;
    private String referenceNumber;
}
