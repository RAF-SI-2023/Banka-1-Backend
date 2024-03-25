package rs.edu.raf.banka1.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import rs.edu.raf.banka1.model.entities.Exchange;

@Entity
@Getter
@Setter
public class ListingStock extends ListingBase {
    @Column
    private Integer outstandingShares;
    @Column
    private Double dividendYield;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "exchange_id", referencedColumnName = "id")
    private Exchange exchange;
}
