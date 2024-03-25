package rs.edu.raf.banka1.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
}
