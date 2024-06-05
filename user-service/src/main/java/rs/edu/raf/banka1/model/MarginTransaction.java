package rs.edu.raf.banka1.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class MarginTransaction {
    @Id
    private Long id;

    private LocalDateTime dateTime = LocalDateTime.now();

    @ManyToOne()
    @JoinColumn(name = "order_id")
    private MarketOrder order;

    @ManyToOne()
    @JoinColumn(name = "customer_user_id")
    private MarginAccount customerAccount;

    private String description;

    @ManyToOne()
    @JoinColumn(name = "currency_id")
    private Currency currency;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    private Double deposit;

    private Double loanValue;

    private Double maintenanceMargin;

    private Double interest;

    private Double capitalAmount;

}
