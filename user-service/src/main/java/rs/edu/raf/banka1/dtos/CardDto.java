package rs.edu.raf.banka1.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CardDto {
    private Long id;
    private String cardNumber;
    private String cardType;
    private String cardName;
    private Long creationDate;
    private Long expirationDate;
    private String accountNumber;
    private String cvv;
    private Integer cardLimit;

    private Boolean isActivated;
}
