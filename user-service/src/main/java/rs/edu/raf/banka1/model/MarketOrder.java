package rs.edu.raf.banka1.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class MarketOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long stockId;
    private Long agentId;
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
    private Boolean done;
    @ManyToOne
    @JoinColumn(name = "approved_by_id")
    private User approvedBy;
}
