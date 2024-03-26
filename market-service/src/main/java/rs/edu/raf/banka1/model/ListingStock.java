package rs.edu.raf.banka1.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ListingStock extends ListingBase {
    @Column
    private Integer outstandingShares;
    @Column
    private Double dividendYield;
}
