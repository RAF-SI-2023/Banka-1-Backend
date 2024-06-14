package rs.edu.raf.banka1.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Getter
@Setter
@ToString
public class MarketOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long listingId;
    private ListingType listingType;

    //market order can be related to an employee or a customer
    @ManyToOne()
    private Employee owner;
    @ManyToOne()
    private Customer customer;
    private OrderType orderType;
    private OrderStatus status;
    private Long contractSize;
    private Long processedNumber;
    private Double limitValue;
    private Double stopValue;
    private Double fee;
    private Double price;
    private Boolean allOrNone;
    private Boolean isMargin = false;

    @UpdateTimestamp
    private Instant updatedAt;

    @Version
    private Integer version;

    @ManyToOne()
    private Employee approvedBy;

    private Long timestamp;
    private Long currentAmount = 0l;
    //we only keep this in case a order is being processed when the server crashes
    private String bankAccountNumber;
}
