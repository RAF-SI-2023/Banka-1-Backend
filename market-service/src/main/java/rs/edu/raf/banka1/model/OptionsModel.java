package rs.edu.raf.banka1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import rs.edu.raf.banka1.model.enums.OptionType;

@Entity
@Getter
@Setter
public class OptionsModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String ticker;
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
