package rs.edu.raf.banka1.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Getter
@Setter
public class OptionsModel extends ListingBase implements Serializable{
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
