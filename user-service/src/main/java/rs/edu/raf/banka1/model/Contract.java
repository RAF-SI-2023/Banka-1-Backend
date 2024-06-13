package rs.edu.raf.banka1.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(cascade = CascadeType.MERGE, optional = false)
    @JoinColumn(name = "buyer_id", nullable = false)
    private BankAccount buyer;
    @ManyToOne(cascade = CascadeType.MERGE, optional = false)
    @JoinColumn(name = "seller_id", nullable = false)
    private BankAccount seller;
    private Boolean bankApproval;
    private Boolean sellerApproval;
    private String comment;
    private Long creationDate;
    private Long realizationDate;
    private String referenceNumber;
    private String ticker;
    private Double amount;
    private Double price;
    private Long listingId;
    @Enumerated(EnumType.STRING)
    private ListingType listingType;
}
