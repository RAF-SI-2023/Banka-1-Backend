package rs.edu.raf.banka1.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
public class MarketOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long stockId;

    @ManyToOne()
    private Employee owner;
    private OrderType orderType;
    private OrderStatus status;
    private Long contractSize;
    private Long processedNumber;
    private Double limitValue;
    private Double stopValue;
    private Double fee;
    private Double price;
    private Boolean allOrNone;
    private Long lastModifiedDate;

    @ManyToOne()
    private Employee approvedBy;
}
