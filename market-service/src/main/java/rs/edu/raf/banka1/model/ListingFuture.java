package rs.edu.raf.banka1.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ListingFuture extends ListingBase {
    @Column
    private Integer contractSize;
    @Column
    private String contractUnit;
    @Column
    private Integer openInterest;
    @Column
    private Integer settlementDate;
}
