package rs.edu.raf.banka1.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Getter
@Setter
public class ListingFuture extends ListingBase implements Serializable {
    @Column
    private Integer contractSize;
    @Column
    private String contractUnit;
    @Column
    private Integer openInterest;
    @Column
    private Integer settlementDate;
    @Column
    private String alternativeTicker;
}
