package rs.edu.raf.banka1.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ListingForex extends ListingBase {
    @Column
    private String baseCurrency;
    @Column
    private String quoteCurrency;
}
