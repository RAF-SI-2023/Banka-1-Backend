package rs.edu.raf.banka1.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rs.edu.raf.banka1.model.entities.Exchange;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ListingStock extends ListingBase {
    @Column
    private Integer outstandingShares;
    @Column
    private Double dividendYield;
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "exchange_id", referencedColumnName = "id")
    private Exchange exchange;

}
