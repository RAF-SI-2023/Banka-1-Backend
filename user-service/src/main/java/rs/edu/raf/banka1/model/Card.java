package rs.edu.raf.banka1.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 16, unique = true)
    private String cardNumber;
    private String cardType;
    private String cardName;
    private Long creationDate;
    private Long expirationDate;
    private String accountNumber;
    @Column(length = 3)
    private String cvv;
    private Integer cardLimit;
    private Boolean isActivated;



}
