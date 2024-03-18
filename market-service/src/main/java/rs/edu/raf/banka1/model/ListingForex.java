package rs.edu.raf.banka1.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ListingForex extends ListingBase{
    @Column
    private String baseCurrency;
    @Column
    private String quoteCurrency;
}
