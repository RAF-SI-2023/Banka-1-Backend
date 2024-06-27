package rs.edu.raf.banka1.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;

@Entity
@Getter
@Setter
public class Capital {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private BankAccount bankAccount;

    private ListingType listingType;

    private Long listingId;

    private String ticker;

    private Double publicTotal = 0d;

    private Double total = 0d;

    private Double reserved;

    private Double averageBuyingPrice = 0d;

    @UpdateTimestamp
    private Instant lastModified;
}
