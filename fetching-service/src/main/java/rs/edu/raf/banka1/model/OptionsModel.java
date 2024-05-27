package rs.edu.raf.banka1.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class OptionsModel extends ListingBase{
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//    @Column
//    private String ticker;
    @Column
    private String optionType;
    @Column
    private Double strikePrice;
    @Column
    private String currency;
    @Column
    private Double impliedVolatility;

    @Column
    private Integer openInterest;
    @Column
    private Long expirationDate;
}
