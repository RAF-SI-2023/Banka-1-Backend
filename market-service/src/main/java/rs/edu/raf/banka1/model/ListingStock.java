package rs.edu.raf.banka1.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
